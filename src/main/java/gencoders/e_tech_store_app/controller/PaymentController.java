package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentMethod;
import gencoders.e_tech_store_app.model.PaymentStatus;
import gencoders.e_tech_store_app.service.processor.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final CreditCardPaymentProcessor creditCardProcessor;
    private final PaypalPaymentProcessor paypalProcessor;
    private final BankTransferPaymentProcessor bankTransferProcessor;
    private final MtnMobileMoneyPaymentProcessor mtnMoMoProcessor;
    private final AirtelMoneyPaymentProcessor airtelProcessor;

    @PostMapping
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentMethod method = request.getPaymentMethod();
        log.info("Processing payment with method: {}", method);

        Payment payment = new Payment();
        payment.setId(System.currentTimeMillis());
        payment.setTransactionId(UUID.randomUUID().toString());
        payment.setAmount(request.getAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCurrency(request.getCurrencyCode());
        payment.setCountryCode(request.getCountryCode());
        payment.setMobileNumber(request.getMobileNumber());
        payment.setMethod(method);

        switch (method) {
            case CREDIT_CARD:
                return ResponseEntity.ok(creditCardProcessor.processPayment(request, payment));
            case PAYPAL:
            case PAYPAL_CREDIT:
                return ResponseEntity.ok(paypalProcessor.processPayment(request, payment));
            case BANK_TRANSFER:
                return ResponseEntity.ok(bankTransferProcessor.processPayment(request, payment));
            case MTN_MOBILE_MONEY:
                return ResponseEntity.ok(mtnMoMoProcessor.processPayment(request, payment));
            case AIRTEL_MONEY:
                return ResponseEntity.ok(airtelProcessor.processPayment(request, payment));
            default:
                return ResponseEntity.badRequest().body(errorResponse("Unsupported payment method"));
        }
    }

    private PaymentResponse errorResponse(String message) {
        PaymentResponse response = new PaymentResponse();
        response.setStatus(PaymentStatus.FAILED);
        response.setMessage(message);
        response.setPaymentDate(LocalDateTime.now());
        return response;
    }
}