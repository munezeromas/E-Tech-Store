package gencoders.e_tech_store_app.product;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class ProductRequest {
    @NotBlank @Size(max = 100) private String name;
    @NotBlank @Size(max = 500) private String description;
    @NotNull @Positive private BigDecimal price;
    @PositiveOrZero private BigDecimal discountPrice = BigDecimal.ZERO;
    @NotNull @PositiveOrZero private Integer stockQuantity;
    @NotBlank private String imageUrl;
    private Set<String> additionalImages;
    @NotNull private Long categoryId;
    private Set<ProductSpecification> specifications;

    // New shared attributes (plain text; service resolves to option tables)
    private String memory;          // e.g. "128GB"
    private String screenType;      // e.g. "OLED"
    private String protection;      // e.g. "IP68"
    private String batteryCapacity; // e.g. "4500mAh"

    private Boolean featured = false;
    private Boolean active   = true;
    private String brand;
    private String model;
    private String screenSize;
}