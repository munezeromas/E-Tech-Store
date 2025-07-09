package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Payment;
import gencoders.e_tech_store_app.model.PaymentStatus;
import gencoders.e_tech_store_app.model.PaymentMethod;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

     List<Payment> findByOrderId(Long orderId);

     Page<Payment> findByOrderId(Long orderId, Pageable pageable);

     List<Payment> findByStatus(PaymentStatus status);

     Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

     Optional<Payment> findByTransactionId(String transactionId);

     List<Payment> findByMethod(PaymentMethod method);

     Page<Payment> findByMethod(PaymentMethod method, Pageable pageable);

     Optional<Payment> findByGatewayTransactionId(String gatewayTransactionId);

     @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
     List<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);

     @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
     Page<Payment> findByPaymentDateBetween(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);

     @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.status = :status")
     Optional<Payment> findByOrderIdAndStatus(@Param("orderId") Long orderId,
                                              @Param("status") PaymentStatus status);

     @Query("SELECT p FROM Payment p WHERE p.amount BETWEEN :minAmount AND :maxAmount")
     List<Payment> findByAmountBetween(@Param("minAmount") BigDecimal minAmount,
                                       @Param("maxAmount") BigDecimal maxAmount);

     @Query("SELECT p FROM Payment p WHERE p.status = 'FAILED' AND p.paymentDate BETWEEN :startDate AND :endDate")
     List<Payment> findFailedPaymentsBetween(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);

     @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' AND p.paymentDate BETWEEN :startDate AND :endDate")
     List<Payment> findSuccessfulPaymentsBetween(@Param("startDate") LocalDateTime startDate,
                                                 @Param("endDate") LocalDateTime endDate);

     @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :cutoffDate")
     List<Payment> findPendingPaymentsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

     @Query("SELECT p FROM Payment p WHERE p.status = 'COMPLETED' AND " +
             "(p.refundAmount IS NULL OR p.refundAmount < p.amount)")
     List<Payment> findRefundablePayments();

     @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = :status")
     BigDecimal getTotalAmountByStatus(@Param("status") PaymentStatus status);

     @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.method = :method")
     BigDecimal getTotalAmountByMethod(@Param("method") PaymentMethod method);

     @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
     BigDecimal getTotalAmountBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);

     long countByStatus(PaymentStatus status);

     long countByMethod(PaymentMethod method);

     @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
     long countPaymentsBetween(@Param("startDate") LocalDateTime startDate,
                               @Param("endDate") LocalDateTime endDate);

     @Query("SELECT p.method, COUNT(p), SUM(p.amount) FROM Payment p GROUP BY p.method")
     List<Object[]> getPaymentStatsByMethod();

     @Query("SELECT p.status, COUNT(p), SUM(p.amount) FROM Payment p GROUP BY p.status")
     List<Object[]> getPaymentStatsByStatus();

     @Query("SELECT DATE(p.paymentDate), COUNT(p), SUM(p.amount) FROM Payment p " +
             "WHERE p.paymentDate BETWEEN :startDate AND :endDate GROUP BY DATE(p.paymentDate)")
     List<Object[]> getDailyPaymentStats(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);

     @Modifying
     @Transactional
     @Query("UPDATE Payment p SET p.status = :status, p.updatedAt = :updatedAt WHERE p.id = :paymentId")
     int updatePaymentStatus(@Param("paymentId") Long paymentId,
                             @Param("status") PaymentStatus status,
                             @Param("updatedAt") LocalDateTime updatedAt);

     @Modifying
     @Transactional
     @Query("UPDATE Payment p SET p.gatewayResponse = :gatewayResponse, " +
             "p.gatewayTransactionId = :gatewayTransactionId, p.updatedAt = :updatedAt " +
             "WHERE p.id = :paymentId")
     int updatePaymentGatewayInfo(@Param("paymentId") Long paymentId,
                                  @Param("gatewayResponse") String gatewayResponse,
                                  @Param("gatewayTransactionId") String gatewayTransactionId,
                                  @Param("updatedAt") LocalDateTime updatedAt);

     boolean existsByTransactionId(String transactionId);

     boolean existsByGatewayTransactionId(String gatewayTransactionId);

     @Query("SELECT p FROM Payment p WHERE p.order.id = :orderId AND p.amount = :amount " +
             "AND p.createdAt > :cutoffTime AND p.status IN ('PENDING', 'PROCESSING', 'COMPLETED')")
     List<Payment> findPotentialDuplicates(@Param("orderId") Long orderId,
                                           @Param("amount") BigDecimal amount,
                                           @Param("cutoffTime") LocalDateTime cutoffTime);
}