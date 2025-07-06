package gencoders.e_tech_store_app.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BlogPostDTO {
    private Long id;
    private String title;
    private String content;
    private String author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String imageUrl;
    private String tags;
    private String slug;
    private String excerpt;
    private Long createdByAdminId;
    private List<CommentDTO> comments;
}