// ReviewResponse.java
package gencoders.e_tech_store_app.review;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewResponse(
        Long id,
        String author,
        String content,
        int rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<ReviewCommentResponse> comments
) { }
