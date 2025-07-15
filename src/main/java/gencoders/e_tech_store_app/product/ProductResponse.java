package gencoders.e_tech_store_app.product;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal discountedPrice;
    private String imageUrl;
    private Set<String> additionalImages;
    private Boolean featured;
    private Boolean active;
    private double averageRating;
    private int reviewCount;
    private boolean inWishlist;
    private LocalDateTime createdAt;
}