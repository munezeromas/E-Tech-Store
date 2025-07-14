// ReviewDto.java  (create / update request)
package gencoders.e_tech_store_app.review;

import jakarta.validation.constraints.*;

public record ReviewDto(
        @NotNull Long productId,
        @NotBlank @Size(max = 2_000) String content,
        @Min(1) @Max(5) int rating
) { }
