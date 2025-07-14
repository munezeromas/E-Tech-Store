package gencoders.e_tech_store_app.wishlist;

import gencoders.e_tech_store_app.product.Product;
import gencoders.e_tech_store_app.product.ProductPreviewDto;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class WishlistResponse {
    private Long id;
    private Set<ProductPreviewDto> products;

    public static WishlistResponse fromWishlist(Set<Product> products) {
        WishlistResponse response = new WishlistResponse();
        response.setProducts(products.stream()
                .map(ProductPreviewDto::new)
                .collect(Collectors.toSet()));
        return response;
    }
}