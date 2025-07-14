// ReviewCommentDto.java
package gencoders.e_tech_store_app.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCommentDto(
        @NotNull Long reviewId,
        @NotBlank @Size(max = 1_000) String content
) { }
