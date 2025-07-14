package gencoders.e_tech_store_app.order;

import lombok.Data;

import java.math.BigDecimal;

@Data
class OrderItemDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal total;
}
