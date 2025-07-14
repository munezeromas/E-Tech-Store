// ReviewController.java
package gencoders.e_tech_store_app.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /* --- create ---------------------------------------------------- */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Add a new review")
    public ResponseEntity<ReviewResponse> add(
            @Valid @RequestBody ReviewDto dto,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(reviewService.addReview(dto, user.getUsername()));
    }

    /* --- read (paged) ---------------------------------------------- */
    @GetMapping("/product/{productId}")
    @Operation(summary = "Paged list of reviews for a product")
    public ResponseEntity<Page<ReviewResponse>> list(
            @PathVariable Long productId,
            Pageable pageable) {

        return ResponseEntity.ok(reviewService.getReviews(productId, pageable));
    }

    /* --- rating stats ---------------------------------------------- */
    @GetMapping("/product/{productId}/stats")
    @Operation(summary = "Rating stats for widget")
    public ResponseEntity<ReviewStatsResponse> stats(
            @PathVariable Long productId) {

        return ResponseEntity.ok(reviewService.getStats(productId));
    }

    /* --- update ---------------------------------------------------- */
    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Edit own review")
    public ResponseEntity<ReviewResponse> edit(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewDto dto,
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                reviewService.updateReview(reviewId, dto, user.getUsername(), dto.productId()));
    }

    /* --- delete ---------------------------------------------------- */
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete own review")
    public ResponseEntity<Void> delete(
            @PathVariable Long reviewId,
            @RequestParam Long productId,
            @AuthenticationPrincipal UserDetails user) {

        reviewService.deleteReview(reviewId, user.getUsername(), productId);
        return ResponseEntity.noContent().build();
    }
}
