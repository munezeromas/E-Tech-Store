package gencoders.e_tech_store_app.order;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderPaymentResponse {
    private Long orderId;
    private String orderNumber;
    private BigDecimal totalAmount;
    private String currency;
    private String paymentReferenceId;
    private String message;
    private boolean success;
}