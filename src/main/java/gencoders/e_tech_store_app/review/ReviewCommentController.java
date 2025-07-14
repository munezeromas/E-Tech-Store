// ReviewCommentController.java
package gencoders.e_tech_store_app.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews/comments")
@Tag(name = "Review Comments")
@RequiredArgsConstructor
public class ReviewCommentController {

    private final ReviewCommentService commentService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add a comment on a review")
    public ResponseEntity<ReviewCommentResponse> addComment(
            @Valid @RequestBody ReviewCommentDto dto,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(commentService.addComment(dto, user.getUsername()));
    }
}
