package gencoders.e_tech_store_app.publiccontrollers;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemRequest {
    private Long productId;
    private int quantity;

}