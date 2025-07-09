package gencoders.e_tech_store_app.service.processor;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;

public interface PaymentProcessor {
    PaymentResponse processPayment(PaymentRequest request, Payment payment);
    void validatePaymentRequest(PaymentRequest request);
}