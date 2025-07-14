package gencoders.e_tech_store_app.blog;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private Long postId;
}