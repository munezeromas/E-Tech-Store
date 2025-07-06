package gencoders.e_tech_store_app.model;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(length = 512)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductSpecification> specifications = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addSpecification(ProductSpecification specification) {
        specifications.add(specification);
        specification.setProduct(this);
    }

    public void removeSpecification(ProductSpecification specification) {
        specifications.remove(specification);
        specification.setProduct(null);
    }

    public BigDecimal getDiscountedPrice() {
        return discountPrice.compareTo(BigDecimal.ZERO) > 0 ?
                discountPrice : price;
    }

    public boolean isInStock() {
        return stockQuantity > 0;
    }
}