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
public class BankTransferPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Payment payment) {
        log.info("Processing bank transfer payment for transaction: {}", payment.getTransactionId());

        validatePaymentRequest(request);

        String bankReference = "BANK-" + UUID.randomUUID().toString();

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionId(bankReference);
        response.setStatus(PaymentStatus.PROCESSING); // Bank transfers often take time
        response.setAmount(payment.getAmount());
        response.setCurrencyCode(payment.getCurrency());
        response.setMessage("Bank transfer initiated");
        response.setPaymentDate(LocalDateTime.now());
        response.setGatewayResponse("Bank Reference: " + bankReference);
        response.setPaymentMethod(payment.getMethod().name());
        response.setBankTransferReference(bankReference);

        return response;
    }

    @Override
    public void validatePaymentRequest(PaymentRequest request) {
        if (request.getCurrencyCode() == null || request.getCurrencyCode().isEmpty()) {
            throw new IllegalArgumentException("Currency code is required");
        }
    }
}