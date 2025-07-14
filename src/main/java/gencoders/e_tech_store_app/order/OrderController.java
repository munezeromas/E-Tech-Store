package gencoders.e_tech_store_app.order;

import gencoders.e_tech_store_app.payment.Payment;
import gencoders.e_tech_store_app.payment.PaymentDto;
import gencoders.e_tech_store_app.payment.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{orderId}/payments")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long orderId,
            @RequestBody PaymentDto paymentDto,
            @RequestParam Long userId) {

        Payment payment = orderService.processPayment(orderId, paymentDto, userId);
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getMethod().name());
        response.setStatus(payment.getStatus().name());
        response.setTransactionId(payment.getTransactionId());
        response.setPaymentDate(payment.getPaymentDate());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId, @RequestParam Long userId) {
        return ResponseEntity.ok(orderService.getOrderByIdAndUser(orderId, userId));
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request, @RequestParam Long userId) {
        return ResponseEntity.ok(orderService.createOrder(userId, request));
    }

    @PostMapping("/rwanda")
    public ResponseEntity<Order> createRwandaOrder(@RequestBody RwandaOrderRequest request, @RequestParam Long userId) {
        return ResponseEntity.ok(orderService.createRwandaOrder(userId, request));
    }
}