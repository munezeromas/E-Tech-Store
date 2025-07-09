package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Order;
import gencoders.e_tech_store_app.model.OrderStatus;
import gencoders.e_tech_store_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Find all orders by user
    List<Order> findByUser(User user);

    // Find order by order number
    Optional<Order> findByOrderNumber(String orderNumber);

    // Find orders by status
    List<Order> findByStatus(OrderStatus status);

    // Custom query to find orders within a date range
    @Query("SELECT o FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Custom query to find orders by user and status
    List<Order> findByUserAndStatus(User user, OrderStatus status);

    // Check if an order number exists
    boolean existsByOrderNumber(String orderNumber);
}