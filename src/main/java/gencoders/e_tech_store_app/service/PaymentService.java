package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.dto.PaymentRequest;
import gencoders.e_tech_store_app.dto.PaymentResponse;
import gencoders.e_tech_store_app.model.*;
import gencoders.e_tech_store_app.repository.PaymentRepository;
import gencoders.e_tech_store_app.repository.OrderRepository;
import gencoders.e_tech_store_app.exception.PaymentException;
import gencoders.e_tech_store_app.service.processor.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CreditCardPaymentProcessor creditCardProcessor;
    private final PaypalPaymentProcessor paypalProcessor;
    private final BankTransferPaymentProcessor bankTransferProcessor;
    private final MtnMobileMoneyPaymentProcessor mtnMobileMoneyProcessor;
    private final AirtelMoneyPaymentProcessor airtelMoneyProcessor;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        log.info("Processing payment for order ID: {}", request.getOrderId());

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new PaymentException("Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setCurrency(request.getCurrencyCode());
        payment.setStatus(PaymentStatus.PROCESSING);
        payment.setTransactionId(generateTransactionId());
        payment.setCountryCode(request.getCountryCode());

        payment = paymentRepository.save(payment);

        try {
            PaymentResponse response = processPaymentByMethod(request, payment);

            payment.setStatus(response.getStatus());
            payment.setGatewayResponse(response.getGatewayResponse());
            payment.setPaymentDate(response.getPaymentDate());
            paymentRepository.save(payment);

            return response;

        } catch (Exception e) {
            log.error("Payment processing failed for transaction: {}", payment.getTransactionId(), e);
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(e.getMessage());
            paymentRepository.save(payment);

            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }

    private PaymentResponse processPaymentByMethod(PaymentRequest request, Payment payment) {
        switch (request.getPaymentMethod()) {
            case CREDIT_CARD:
                return creditCardProcessor.processPayment(request, payment);
            case PAYPAL:
                return paypalProcessor.processPayment(request, payment);
            case PAYPAL_CREDIT:
                return paypalProcessor.processPayment(request, payment);
            case MTN_MOBILE_MONEY:
                return mtnMobileMoneyProcessor.processPayment(request, payment);
            case AIRTEL_MONEY:
                return airtelMoneyProcessor.processPayment(request, payment);
            case BANK_TRANSFER:
                return bankTransferProcessor.processPayment(request, payment);
            default:
                throw new PaymentException("Unsupported payment method: " + request.getPaymentMethod());
        }
    }

    public PaymentResponse getPaymentStatus(String transactionId) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentException("Payment not found"));

        return buildPaymentResponse(payment);
    }

    @Transactional
    public PaymentResponse refundPayment(String transactionId, BigDecimal refundAmount) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentException("Payment not found"));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new PaymentException("Can only refund completed payments");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setRefundAmount(refundAmount);
        payment.setRefundDate(LocalDateTime.now());

        paymentRepository.save(payment);

        return buildPaymentResponse(payment);
    }

    private PaymentResponse buildPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(payment.getId());
        response.setTransactionId(payment.getTransactionId());
        response.setStatus(payment.getStatus());
        response.setAmount(payment.getAmount());
        response.setCurrencyCode(payment.getCurrency());
        response.setMessage("Payment status retrieved successfully");
        response.setPaymentDate(payment.getPaymentDate());
        response.setGatewayResponse(payment.getGatewayResponse());
        response.setPaymentMethod(payment.getMethod().name());

        if (payment.getMethod() == PaymentMethod.MTN_MOBILE_MONEY) {
            response.setMobileMoneyReference(payment.getTransactionId());
        }

        return response;
    }

    public List<Payment> getPaymentsByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    public List<Payment> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}