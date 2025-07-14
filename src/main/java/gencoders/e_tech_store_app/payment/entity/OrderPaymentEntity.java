package gencoders.e_tech_store_app.payment.entity;

import gencoders.e_tech_store_app.payment.Mtn.OrderPaymentRequest;
import gencoders.e_tech_store_app.payment.Mtn.OrderPaymentResponse;
import gencoders.e_tech_store_app.payment.Mtn.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Optional: Database entity to store order payment information
 * This helps track payment history and order status
 */
@Entity
@Table(name = "order_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderPaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "momo_reference_id", unique = true)
    private String momoReferenceId;

    @Column(name = "external_id", unique = true)
    private String externalId;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "customer_phone", nullable = false)
    private String customerPhone;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "payment_description")
    private String paymentDescription;

    @Column(name = "order_items", columnDefinition = "TEXT")
    private String orderItems;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "momo_transaction_id")
    private String momoTransactionId;

    @Column(name = "error_reason")
    private String errorReason;

    @Column(name = "payment_initiated_at", nullable = false)
    private LocalDateTime paymentInitiatedAt;

    @Column(name = "payment_completed_at")
    private LocalDateTime paymentCompletedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (paymentInitiatedAt == null) {
            paymentInitiatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (paymentStatus == PaymentStatus.SUCCESSFUL && paymentCompletedAt == null) {
            paymentCompletedAt = LocalDateTime.now();
        }
    }

    /**
     * Creates entity from payment request
     */
    public static OrderPaymentEntity fromPaymentRequest(OrderPaymentRequest request, String momoReferenceId) {
        return OrderPaymentEntity.builder()
                .orderId(request.getOrderId())
                .momoReferenceId(momoReferenceId)
                .externalId(request.getExternalId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .customerPhone(request.getCustomerPhone())
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail())
                .paymentDescription(request.getPaymentDescription())
                .orderItems(request.getOrderItems())
                .paymentStatus(PaymentStatus.PENDING)
                .paymentInitiatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Updates entity with payment response
     */
    public void updateFromPaymentResponse(OrderPaymentResponse response) {
        this.paymentStatus = response.getPaymentStatus();
        this.momoTransactionId = response.getMomoTransactionId();
        this.errorReason = response.getErrorReason();

        if (response.getPaymentCompletedAt() != null) {
            this.paymentCompletedAt = response.getPaymentCompletedAt();
        }
    }

    /**
     * Checks if payment is completed successfully
     */
    public boolean isPaymentSuccessful() {
        return PaymentStatus.SUCCESSFUL.equals(paymentStatus);
    }

    /**
     * Checks if payment has failed
     */
    public boolean isPaymentFailed() {
        return paymentStatus != null && paymentStatus.isFailure();
    }

    /**
     * Checks if payment is still pending
     */
    public boolean isPaymentPending() {
        return paymentStatus != null && paymentStatus.isInProgress();
    }
}