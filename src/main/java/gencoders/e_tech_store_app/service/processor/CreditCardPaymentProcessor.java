package gencoders.e_tech_store_app.service.processor;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class CreditCardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse processPayment(PaymentRequest request, Payment payment) {
        log.info("Processing credit card payment for transaction: {}", payment.getTransactionId());

        String maskedCardNumber = maskCardNumber(request.getCardNumber());
        String authCode = generateAuthCode();

        return buildSuccessResponse(payment, maskedCardNumber, authCode);
    }

    @Override
    public void validatePaymentRequest(PaymentRequest request) {
        if (request.getCardNumber() == null || request.getCardNumber().length() < 16) {
            throw new IllegalArgumentException("Invalid card number");
        }

        if (request.getCvv() == null || request.getCvv().length() < 3) {
            throw new IllegalArgumentException("Invalid CVV");
        }

        if (request.getExpiryDate() == null || !request.getExpiryDate().matches("^(0[1-9]|1[0-2])\\/([0-9]{2})$")) {
            throw new IllegalArgumentException("Invalid expiry date format (MM/YY)");
        }
    }

    private String maskCardNumber(String cardNumber) {
        return "****-****-****-" + cardNumber.substring(cardNumber.length() - 4);
    }

    private String generateAuthCode() {
        return "AUTH" + System.currentTimeMillis() % 100000;
    }

    private PaymentResponse buildSuccessResponse(Payment payment, String maskedCardNumber, String authCode) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionId(payment.getTransactionId());
        response.setStatus(PaymentStatus.COMPLETED);
        response.setAmount(payment.getAmount());
        response.setCurrencyCode(payment.getCurrency());
        response.setMessage("Credit card payment processed successfully");
        response.setPaymentDate(LocalDateTime.now());
        response.setGatewayResponse("Auth Code: " + authCode);
        response.setPaymentMethod(payment.getMethod().name());
        response.setMaskedCardNumber(maskedCardNumber);
        return response;
    }
}