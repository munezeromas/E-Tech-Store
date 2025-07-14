// src/main/java/gencoders/e_tech_store_app/product/ProductRequest.java
package gencoders.e_tech_store_app.product;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO used by both create & update product endpoints.
 * The image URLs are obtained after calling /api/uploads/image.
 */
@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500)
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private BigDecimal discountPrice = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    private Integer stockQuantity;

    /** Cloudinary URL of the main image. */
    @NotBlank
    private String imageUrl;

    /** Optional gallery images (Cloudinary URLs). */
    private Set<String> additionalImages;

    @NotNull
    private Long categoryId;

    private Set<ProductSpecification> specifications;
}
