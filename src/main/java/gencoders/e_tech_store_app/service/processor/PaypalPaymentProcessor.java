package gencoders.e_tech_store_app.service.processor;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
public class PaypalPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Payment payment) {
        log.info("Processing PayPal payment for transaction: {}", payment.getTransactionId());

        validatePaymentRequest(request);

        String paypalTransactionId = "PAYPAL-" + UUID.randomUUID().toString();

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionId(paypalTransactionId);
        response.setStatus(PaymentStatus.COMPLETED);
        response.setAmount(payment.getAmount());
        response.setCurrencyCode(payment.getCurrency());
        response.setMessage("PayPal payment processed successfully");
        response.setPaymentDate(LocalDateTime.now());
        response.setGatewayResponse("PayPal Response: APPROVED");
        response.setPaymentMethod(payment.getMethod().name());

        return response;
    }

    @Override
    public void validatePaymentRequest(PaymentRequest request) {
        if (request.getCurrencyCode() == null || request.getCurrencyCode().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }
}