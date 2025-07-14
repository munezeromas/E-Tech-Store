package gencoders.e_tech_store_app.order;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private Long addressId;
    private String paymentMethod;
}