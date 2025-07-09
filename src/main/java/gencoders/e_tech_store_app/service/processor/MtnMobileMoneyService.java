package gencoders.e_tech_store_app.service.processor;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class MtnMobileMoneyService implements PaymentProcessor {

    @Value("${mtn.mobile-money.api-key}")
    private String apiKey;

    @Value("${mtn.mobile-money.merchant-id}")
    private String merchantId;

    @Value("${mtn.mobile-money.callback-url}")
    private String callbackUrl;

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Payment payment) {
        validatePaymentRequest(request);

        String transactionId = "MTN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionId(transactionId);
        response.setStatus(PaymentStatus.PROCESSING);
        response.setAmount(payment.getAmount());
        response.setCurrencyCode(payment.getCurrency());
        response.setMessage("Payment request sent to mobile number " + request.getMobileNumber());
        response.setPaymentDate(LocalDateTime.now());
        response.setGatewayResponse("MTN Response: PENDING_USER_CONFIRMATION");
        response.setPaymentMethod(payment.getMethod().name());
        response.setMobileMoneyReference(transactionId);

        return response;
    }

    @Override
    public void validatePaymentRequest(PaymentRequest request) {
        if (request.getMobileNumber() == null || !request.getMobileNumber().matches("^07[258]\\d{7}$")) {
            throw new IllegalArgumentException("Invalid MTN Rwanda mobile number format. Must be 07[2,5,8]XXXXXXX");
        }

        if (!"RWF".equals(request.getCurrencyCode())) {
            throw new IllegalArgumentException("MTN Mobile Money payments must be in RWF");
        }
    }
}