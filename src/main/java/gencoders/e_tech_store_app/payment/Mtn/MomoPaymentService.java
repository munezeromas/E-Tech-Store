package gencoders.e_tech_store_app.payment.Mtn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomoPaymentService {

    private final MomoAuthService authService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${momo.base-url}")
    private String baseUrl;

    @Value("${momo.api.key}")
    private String apiKey;

    @Value("${momo.environment}")
    private String environment;

    @Value("${momo.admin-msisdn}")
    private String adminMsisdn;

    /**
     * Processes order payment request through MTN MoMo
     * Customer pays to admin account
     */
    public OrderPaymentResponse processOrderPayment(OrderPaymentRequest paymentRequest) {
        log.info("Processing order payment for Order ID: {} - Amount: {} {}",
                paymentRequest.getOrderId(), paymentRequest.getAmount(), paymentRequest.getCurrency());

        try {
            // Validate request
            if (!paymentRequest.isValid()) {
                return OrderPaymentResponse.createFailedResponse(
                        paymentRequest.getOrderId(),
                        "Invalid payment request data",
                        paymentRequest.getCustomerPhone(),
                        paymentRequest.getCustomerName()
                );
            }

            String referenceId = UUID.randomUUID().toString();

            // Prepare MoMo request body - Customer pays to Admin
            Map<String, Object> requestBody = createMomoRequestBody(paymentRequest, referenceId);

            // Prepare headers
            HttpHeaders headers = createMomoHeaders(referenceId);

            // Make payment request to MTN MoMo API
            ResponseEntity<String> response = restTemplate.exchange(
                    baseUrl + "/collection/v1_0/requesttopay",
                    HttpMethod.POST,
                    new HttpEntity<>(requestBody, headers),
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
                log.info("Payment request accepted for Order ID: {} - MoMo Reference: {}",
                        paymentRequest.getOrderId(), referenceId);

                return OrderPaymentResponse.createSuccessResponse(
                        paymentRequest.getOrderId(),
                        referenceId,
                        paymentRequest.getExternalId(),
                        paymentRequest.getAmount(),
                        paymentRequest.getCurrency(),
                        paymentRequest.getCustomerPhone(),
                        paymentRequest.getCustomerName()
                );
            } else {
                log.warn("Payment request failed with status: {} for Order ID: {}",
                        response.getStatusCode(), paymentRequest.getOrderId());

                return OrderPaymentResponse.createFailedResponse(
                        paymentRequest.getOrderId(),
                        "Payment request failed with status: " + response.getStatusCode(),
                        paymentRequest.getCustomerPhone(),
                        paymentRequest.getCustomerName()
                );
            }

        } catch (HttpClientErrorException e) {
            log.error("MTN MoMo API error for Order ID: {} - Status: {} - Response: {}",
                    paymentRequest.getOrderId(), e.getStatusCode(), e.getResponseBodyAsString());

            return OrderPaymentResponse.createFailedResponse(
                    paymentRequest.getOrderId(),
                    "MTN MoMo service error: " + e.getMessage(),
                    paymentRequest.getCustomerPhone(),
                    paymentRequest.getCustomerName()
            );

        } catch (ResourceAccessException e) {
            log.error("Network error while processing payment for Order ID: {} - {}",
                    paymentRequest.getOrderId(), e.getMessage());

            return OrderPaymentResponse.createFailedResponse(
                    paymentRequest.getOrderId(),
                    "Network error: Unable to connect to MTN MoMo service",
                    paymentRequest.getCustomerPhone(),
                    paymentRequest.getCustomerName()
            );

        } catch (Exception e) {
            log.error("Unexpected error processing payment for Order ID: {} - {}",
                    paymentRequest.getOrderId(), e.getMessage(), e);

            return OrderPaymentResponse.createFailedResponse(
                    paymentRequest.getOrderId(),
                    "System error: " + e.getMessage(),
                    paymentRequest.getCustomerPhone(),
                    paymentRequest.getCustomerName()
            );
        }
    }

    /**
     * Checks the status of a payment using MoMo reference ID
     */
    public OrderPaymentResponse checkOrderPaymentStatus(String orderId, String momoReferenceId) {
        log.info("Checking payment status for Order ID: {} - MoMo Reference: {}", orderId, momoReferenceId);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + authService.getAccessToken());
            headers.set("X-Target-Environment", environment);
            headers.set("Ocp-Apim-Subscription-Key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/collection/v1_0/requesttopay/" + momoReferenceId,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            if (response.getBody() != null) {
                Map<String, Object> body = response.getBody();

                String statusStr = body.get("status") != null ? body.get("status").toString() : "UNKNOWN";
                PaymentStatus status = PaymentStatus.fromMomoStatus(statusStr);

                String momoTransactionId = body.get("financialTransactionId") != null ?
                        body.get("financialTransactionId").toString() : null;

                log.info("Payment status retrieved for Order ID: {} - Status: {}", orderId, status);

                return OrderPaymentResponse.createStatusResponse(orderId, momoReferenceId, status, momoTransactionId);
            } else {
                log.warn("Empty response body when checking payment status for Order ID: {}", orderId);
                return OrderPaymentResponse.createStatusResponse(orderId, momoReferenceId, PaymentStatus.UNKNOWN, null);
            }

        } catch (HttpClientErrorException e) {
            log.error("Error checking payment status for Order ID: {} - Status: {} - Response: {}",
                    orderId, e.getStatusCode(), e.getResponseBodyAsString());

            return OrderPaymentResponse.createStatusResponse(orderId, momoReferenceId, PaymentStatus.FAILED, null);

        } catch (Exception e) {
            log.error("Unexpected error checking payment status for Order ID: {} - {}", orderId, e.getMessage(), e);
            return OrderPaymentResponse.createStatusResponse(orderId, momoReferenceId, PaymentStatus.UNKNOWN, null);
        }
    }

    /**
     * Creates the request body for MTN MoMo payment request
     */
    private Map<String, Object> createMomoRequestBody(OrderPaymentRequest paymentRequest, String referenceId) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", paymentRequest.getAmount().toString());
        requestBody.put("currency", paymentRequest.getCurrency());
        requestBody.put("externalId", paymentRequest.getExternalId());

        // Customer (payer) details
        Map<String, String> payer = new HashMap<>();
        payer.put("partyIdType", "MSISDN");
        payer.put("partyId", paymentRequest.getCustomerPhone());
        requestBody.put("payer", payer);

        // Payment messages
        requestBody.put("payerMessage", paymentRequest.getPayerMessage());
        requestBody.put("payeeNote", paymentRequest.getPayeeNote());

        log.debug("Created MoMo request body for Order ID: {} - Customer: {} pays to Admin",
                paymentRequest.getOrderId(), paymentRequest.getCustomerPhone());

        return requestBody;
    }

    /**
     * Creates headers for MTN MoMo API requests
     */
    private HttpHeaders createMomoHeaders(String referenceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authService.getAccessToken());
        headers.set("X-Reference-Id", referenceId);
        headers.set("X-Target-Environment", environment);
        headers.set("Ocp-Apim-Subscription-Key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    /**
     * Validates if MTN MoMo service is available
     */
    public boolean isServiceAvailable() {
        try {
            authService.getAccessToken();
            return true;
        } catch (Exception e) {
            log.error("MTN MoMo service not available: {}", e.getMessage());
            return false;
        }
    }
}