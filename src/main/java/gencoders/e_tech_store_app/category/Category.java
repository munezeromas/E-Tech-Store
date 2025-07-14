package gencoders.e_tech_store_app.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gencoders.e_tech_store_app.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 100)
    private String slug; // For frontend URLs: phones, smart-watches

    private String imageUrl; // For icons or thumbnails

    @Size(max = 200)
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("category") // Break circular reference

    private Set<Product> products;

    public Category(String name, String slug, String imageUrl, String description) {
        this.name = name;
        this.slug = slug;
        this.imageUrl = imageUrl;
        this.description = description;
    }
}
