package gencoders.e_tech_store_app.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for product image upload
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageResponse {
    private String url;
    private String productId;
    private String message;
    private LocalDateTime timestamp;

    public ProductImageResponse(String url, String productId) {
        this.url = url;
        this.productId = productId;
        this.message = "Product image uploaded successfully";
        this.timestamp = LocalDateTime.now();
    }
}