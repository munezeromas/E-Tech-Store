package gencoders.e_tech_store_app.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank
    private String name;

    private String slug;

    private String imageUrl;

    private String description;
}
