package gencoders.e_tech_store_app.payload.request;

import gencoders.e_tech_store_app.model.ProductSpecification;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(max = 100, message = "Product name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @PositiveOrZero(message = "Discount price must be positive or zero")
    private BigDecimal discountPrice;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be positive or zero")
    private Integer stockQuantity;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private Set<ProductSpecification> specifications;
}