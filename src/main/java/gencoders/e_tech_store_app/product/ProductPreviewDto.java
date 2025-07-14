package gencoders.e_tech_store_app.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPreviewDto {
    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal price;

    public ProductPreviewDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
    }
}
