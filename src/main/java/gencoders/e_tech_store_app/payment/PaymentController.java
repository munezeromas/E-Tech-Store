package gencoders.e_tech_store_app.payment;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/mtn")
    public ResponseEntity<PaymentResponse> processMtnPayment(@RequestBody PaymentDto paymentDto) {
        paymentDto.setPaymentMethod("MTN_MOBILE_MONEY");
        Payment payment = paymentService.processPayment(paymentDto);
        return ResponseEntity.ok(toResponse(payment));
    }

    @PostMapping("/card")
    public ResponseEntity<PaymentResponse> processCardPayment(@RequestBody PaymentDto paymentDto) {
        paymentDto.setPaymentMethod("CREDIT_CARD");
        Payment payment = paymentService.processPayment(paymentDto);
        return ResponseEntity.ok(toResponse(payment));
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getMethod().name());
        response.setStatus(payment.getStatus().name());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());
        return response;
    }
}