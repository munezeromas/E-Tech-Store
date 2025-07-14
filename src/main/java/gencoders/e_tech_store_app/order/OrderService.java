package gencoders.e_tech_store_app.order;

import gencoders.e_tech_store_app.address.Address;
import gencoders.e_tech_store_app.address.AddressRepository;
import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.payment.Payment;
import gencoders.e_tech_store_app.payment.PaymentDto;
import gencoders.e_tech_store_app.payment.PaymentService;
import gencoders.e_tech_store_app.product.Product;
import gencoders.e_tech_store_app.product.ProductRepository;
import gencoders.e_tech_store_app.shoppingcart.ShoppingCart;
import gencoders.e_tech_store_app.shoppingcart.ShoppingCartService;
import gencoders.e_tech_store_app.user.User;
import gencoders.e_tech_store_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ShoppingCartService shoppingCartService;
    private final ProductRepository productRepository;
    private final PaymentService paymentService;     // one‑way dependency (no cycle)

    /* -------------------------------------------------
       PUBLIC API
       ------------------------------------------------- */

    @Transactional
    public Order createOrder(Long userId, OrderRequest orderRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Address shippingAddress = addressRepository.findById(orderRequest.getAddressId())
                .filter(address -> address.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address", "id", orderRequest.getAddressId()));

        ShoppingCart cart = shoppingCartService.getCartByUser(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cannot create order with an empty cart");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(shippingAddress);

        // Convert cart items ➜ order items
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> {
                    OrderItem oi = new OrderItem(ci.getProduct(), ci.getQuantity());
                    oi.setOrder(order);
                    return oi;
                })
                .collect(Collectors.toList());

        order.setItems(new HashSet<>(orderItems));
        order.calculateTotals();
        order.setStatus(OrderStatus.PENDING);

        // Check stock and update
        for (OrderItem item : order.getItems()) {
            Product p = item.getProduct();
            if (p.getStockQuantity() < item.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product: " + p.getName());
            }
            p.setStockQuantity(p.getStockQuantity() - item.getQuantity());
            productRepository.save(p);
        }

        Order savedOrder = orderRepository.save(order);
        shoppingCartService.clearCart(userId);
        return savedOrder;
    }

    @Transactional
    public Payment processPayment(Long orderId, PaymentDto dto, Long userId) {

        Order order = getOrderByIdAndUser(orderId, userId);
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not in PENDING status");
        }

        dto.setOrderId(orderId);
        dto.setAmount(order.getTotal());
        dto.setCurrency("RWF");

        Payment payment = paymentService.processPayment(dto);

        order.setStatus(OrderStatus.PROCESSING);
        orderRepository.save(order);

        return payment;
    }

    public List<Order> getUserOrders(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return orderRepository.findByUser(user);
    }

    public Order getOrderByIdAndUser(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .filter(o -> o.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order", "id", orderId + " for user " + userId));
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);

        if (status == OrderStatus.SHIPPED) {
            order.setDeliveryDate(LocalDateTime.now().plusDays(3));
        } else if (status == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order createRwandaOrder(Long userId, RwandaOrderRequest req) {
        if (!req.getPhoneNumber().startsWith("+250")) {
            throw new IllegalArgumentException("Rwanda phone numbers must start with +250");
        }
        Order order = createOrder(userId, req);
        order.setRwandaTaxCode(req.getTaxCode());
        order.setRwandaPhoneNumber(req.getPhoneNumber());
        return orderRepository.save(order);
    }
}
