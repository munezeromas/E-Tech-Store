package gencoders.e_tech_store_app.payment;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.order.Order;
import gencoders.e_tech_store_app.order.OrderRepository;
import gencoders.e_tech_store_app.product.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository       paymentRepository;
    private final OrderRepository         orderRepository;     // ⬅️  depends on repo, not service
    private final ApplicationEventPublisher eventPublisher;

    /* -------------------------------------------------
       PUBLIC API
       ------------------------------------------------- */

    @Transactional
    public Payment processPayment(PaymentDto dto) {

        Order order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order", "id", dto.getOrderId()));

        Payment payment = createPaymentInstance(dto);
        payment.setOrder(order);
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency());
        payment.setMethod(PaymentMethod.valueOf(dto.getPaymentMethod()));

        boolean success = payment.processPayment();
        payment.setStatus(success ? PaymentStatus.COMPLETED : PaymentStatus.FAILED);
        payment.setPaymentDate(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);

        eventPublisher.publishEvent(new PaymentProcessedEvent(this, saved));
        return saved;
    }

    /* -------------------------------------------------
       HELPER
       ------------------------------------------------- */

    private Payment createPaymentInstance(PaymentDto dto) {
        switch (PaymentMethod.valueOf(dto.getPaymentMethod())) {
            case MTN_MOBILE_MONEY -> {
                MtnPayment p = new MtnPayment();
                p.setPhoneNumber(dto.getPhoneNumber());
                if (dto instanceof RwandaPaymentDto rwandaDto) {
                    p.setRwandaIdNumber(rwandaDto.getNationalId());
                }
                return p;
            }
            case CREDIT_CARD -> {
                CreditCardPayment p = new CreditCardPayment();
                p.setCardNumber(dto.getCardNumber());
                p.setCardHolderName(dto.getCardHolderName());
                p.setExpiryDate(dto.getExpiryDate());
                p.setCvv(dto.getCvv());
                return p;
            }
            default -> throw new IllegalArgumentException("Unsupported payment method");
        }
    }
}
