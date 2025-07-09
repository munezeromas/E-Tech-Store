package gencoders.e_tech_store_app.service.processor;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@Slf4j
public class MtnMobileMoneyPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Payment payment) {
        log.info("Processing MTN Mobile Money payment for transaction: {}", payment.getTransactionId());

        validatePaymentRequest(request);

        // Simulate processing delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Payment processing interrupted");
        }

        String mtnReference = generateMtnReference();

        return buildSuccessResponse(payment, mtnReference);
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

    private String generateMtnReference() {
        return "MTN" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private PaymentResponse buildSuccessResponse(Payment payment, String mtnReference) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionId(payment.getTransactionId());
        response.setStatus(PaymentStatus.COMPLETED);
        response.setAmount(payment.getAmount());
        response.setCurrencyCode(payment.getCurrency());
        response.setMessage("MTN Mobile Money payment processed successfully");
        response.setPaymentDate(LocalDateTime.now());
        response.setGatewayResponse("MTN Response: SUCCESS");
        response.setPaymentMethod(payment.getMethod().name());
        response.setMobileMoneyReference(mtnReference);
        return response;
    }
}