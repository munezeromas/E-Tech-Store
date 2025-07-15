package gencoders.e_tech_store_app.product;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class ProductDetailResponse {
    private ProductResponse product;
    private List<ProductResponse> relatedProducts;
}