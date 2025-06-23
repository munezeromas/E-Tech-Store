package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.dto.ReviewDto;
import gencoders.e_tech_store_app.dto.ReviewResponse;
import gencoders.e_tech_store_app.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Product review endpoints")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Add a review")
    public ResponseEntity<ReviewResponse> addReview(
            @Valid @RequestBody ReviewDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(reviewService.addReview(request, userDetails.getUsername()));
    }
}
