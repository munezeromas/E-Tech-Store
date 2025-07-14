package gencoders.e_tech_store_app.order;

import gencoders.e_tech_store_app.payment.PaymentResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private String orderNumber;
    private String userEmail;
    private List<OrderItemDTO> items;
    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shipping;
    private BigDecimal total;
    private String shippingAddress;
    private String billingAddress;
    private String shippingMethod;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private PaymentResponse payment;
}

