package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.model.Order;
import gencoders.e_tech_store_app.model.OrderStatus;
import gencoders.e_tech_store_app.model.PaymentMethod;
import gencoders.e_tech_store_app.dto.OrderRequest;
import gencoders.e_tech_store_app.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
@PreAuthorize("hasRole('USER')")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestBody OrderRequest orderRequest, @RequestParam Long userId) {
        return orderService.createOrder(userId, orderRequest);
    }

    @GetMapping
    public List<Order> getUserOrders(@RequestParam Long userId) {
        return orderService.getUserOrders(userId);
    }

    @GetMapping("/{id}")
    public Order getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> processPayment(
            @PathVariable Long id,
            @RequestParam String method,
            @RequestParam String transactionId) {
        return ResponseEntity.ok(orderService.processPayment(id, PaymentMethod.valueOf(method), transactionId));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public Order updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return orderService.updateOrderStatus(id, OrderStatus.valueOf(status));
    }
}