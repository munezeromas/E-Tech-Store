package gencoders.e_tech_store_app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequest {
    @NotBlank(message = "Author name is required")
    private String authorName;

    @NotBlank(message = "Comment content is required")
    private String content;
}