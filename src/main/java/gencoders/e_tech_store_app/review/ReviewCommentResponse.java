// ReviewCommentResponse.java
package gencoders.e_tech_store_app.review;

import java.time.LocalDateTime;

public record ReviewCommentResponse(
        Long id,
        String author,
        String content,
        LocalDateTime createdAt
) { }
