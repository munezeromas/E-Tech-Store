package gencoders.e_tech_store_app.product;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gencoders.e_tech_store_app.category.Category;
import gencoders.e_tech_store_app.review.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ─── IMAGES ────────────────────────────────────────────────────
    @Column(length = 512)
    private String mainImage;               // Hero image (optional)
    private String imageUrl;                // Primary image shown in listings

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 512)
    @Builder.Default
    private Set<String> additionalImages = new HashSet<>();

    // ─── CORE INFO ────────────────────────────────────────────────
    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    // ─── CATEGORY ────────────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties("products")
    @JoinColumn(name = "category_id")
    private Category category;

    // ─── SPECS & REVIEWS ─────────────────────────────────────────
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductSpecification> specifications = new HashSet<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Review> reviews = new HashSet<>();

    // ─── FLAGS ───────────────────────────────────────────────────
    @Column(nullable = false) @Builder.Default
    private Boolean active = true;

    @Column(nullable = false) @Builder.Default
    private Boolean featured = false;

    // ─── TIMESTAMPS ──────────────────────────────────────────────
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ─── COMMON ATTRIBUTES (linked to option tables) ─────────────
    @ManyToOne private MemoryOption memory;
    @ManyToOne private ScreenTypeOption screenType;
    @ManyToOne private ProtectionOption protection;
    @ManyToOne private BatteryCapacityOption batteryCapacity;

    @Column(length = 100) private String brand;
    @Column(length = 100) private String model;
    @Column(length = 50)  private String screenSize;   // e.g. 6.1"

    // ─── HELPERS ─────────────────────────────────────────────────
    public void addSpecification(ProductSpecification spec) {
        specifications.add(spec);
        spec.setProduct(this);
    }
    public void removeSpecification(ProductSpecification spec) {
        specifications.remove(spec);
        spec.setProduct(null);
    }

    public BigDecimal getDiscountedPrice() {
        return discountPrice.compareTo(BigDecimal.ZERO) > 0 ? discountPrice : price;
    }

    public boolean isInStock() { return stockQuantity > 0; }
}