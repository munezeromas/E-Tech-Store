package gencoders.e_tech_store_app.payment.Mtn;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payments/momo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MomoPaymentController {

    private final MomoPaymentService momoPaymentService;

    /**
     * Initiates payment for an order via MTN MoMo
     * Customer pays to admin account
     *
     * @param paymentRequest Order payment details
     * @return Payment response with MoMo reference ID
     */
    @PostMapping("/pay-order")
    public ResponseEntity<OrderPaymentResponse> payForOrder(@Valid @RequestBody OrderPaymentRequest paymentRequest) {
        log.info("Received payment request for Order ID: {} - Customer: {} - Amount: {} {}",
                paymentRequest.getOrderId(),
                paymentRequest.getCustomerName(),
                paymentRequest.getAmount(),
                paymentRequest.getCurrency());

        try {
            // Process the payment through MTN MoMo
            OrderPaymentResponse response = momoPaymentService.processOrderPayment(paymentRequest);

            if (response.isSuccess()) {
                log.info("Payment initiated successfully for Order ID: {} - MoMo Reference: {}",
                        paymentRequest.getOrderId(), response.getMomoReferenceId());
                return ResponseEntity.accepted().body(response);
            } else {
                log.warn("Payment initiation failed for Order ID: {} - Reason: {}",
                        paymentRequest.getOrderId(), response.getErrorReason());
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            log.error("Unexpected error processing payment for Order ID: {} - {}",
                    paymentRequest.getOrderId(), e.getMessage(), e);

            OrderPaymentResponse errorResponse = OrderPaymentResponse.createFailedResponse(
                    paymentRequest.getOrderId(),
                    "System error: " + e.getMessage(),
                    paymentRequest.getCustomerPhone(),
                    paymentRequest.getCustomerName()
            );

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Checks the status of an order payment
     *
     * @param orderId Order ID
     * @param momoReferenceId MTN MoMo reference ID
     * @return Current payment status
     */
    @GetMapping("/order-payment-status/{orderId}/{momoReferenceId}")
    public ResponseEntity<OrderPaymentResponse> checkOrderPaymentStatus(
            @PathVariable String orderId,
            @PathVariable String momoReferenceId) {

        log.info("Checking payment status for Order ID: {} - MoMo Reference: {}", orderId, momoReferenceId);

        try {
            OrderPaymentResponse response = momoPaymentService.checkOrderPaymentStatus(orderId, momoReferenceId);

            log.info("Payment status retrieved for Order ID: {} - Status: {}",
                    orderId, response.getPaymentStatus());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking payment status for Order ID: {} - {}", orderId, e.getMessage(), e);

            OrderPaymentResponse errorResponse = OrderPaymentResponse.createStatusResponse(
                    orderId, momoReferenceId, PaymentStatus.UNKNOWN, null);

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }}
