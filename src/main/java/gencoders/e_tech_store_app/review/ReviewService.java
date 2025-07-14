// ReviewService.java
package gencoders.e_tech_store_app.review;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.product.Product;
import gencoders.e_tech_store_app.product.ProductRepository;
import gencoders.e_tech_store_app.user.User;
import gencoders.e_tech_store_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;

    /* ------------------ CRUD ------------------ */

    @Transactional
    @CacheEvict(value = "reviewStats", key = "#dto.productId()")
    public ReviewResponse addReview(ReviewDto dto, String username) {

        Product product = productRepo.findById(dto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Product","id", dto.productId()));

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User","username", username));

        /* One review per user/product */
        reviewRepo.findByProductIdAndUserId(product.getId(), user.getId())
                .ifPresent(r -> { throw new IllegalStateException("You already reviewed this product."); });

        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setContent(dto.content());
        review.setRating(dto.rating());

        return toResponse(reviewRepo.save(review));
    }

    public Page<ReviewResponse> getReviews(Long productId, Pageable pageable) {
        return reviewRepo.findByProductIdOrderByCreatedAtDesc(productId, pageable)
                .map(this::toResponse);
    }

    @Transactional
    @CacheEvict(value = "reviewStats", key = "#productId")
    public ReviewResponse updateReview(Long reviewId, ReviewDto dto, String username, Long productId) {
        Review review = findOwnedReview(reviewId, username);

        review.setContent(dto.content());
        review.setRating(dto.rating());
        review.setUpdatedAt(LocalDateTime.now());
        return toResponse(review);
    }

    @Transactional
    @CacheEvict(value = "reviewStats", key = "#productId")
    public void deleteReview(Long reviewId, String username, Long productId) {
        Review review = findOwnedReview(reviewId, username);
        reviewRepo.delete(review);
    }

    /* ------------------ stats ------------------ */

    @Cacheable(value = "reviewStats", key = "#productId")
    public ReviewStatsResponse getStats(Long productId) {
        double avg = Optional.ofNullable(reviewRepo.avgRating(productId)).orElse(0.0);
        var raw = reviewRepo.ratingCounts(productId);

        Map<Integer, Long> dist = raw.stream()
                .collect(Collectors.toMap(
                        r -> (Integer) r[0],
                        r -> (Long) r[1]));

        IntStream.rangeClosed(1,5).forEach(i -> dist.putIfAbsent(i, 0L));
        long total = dist.values().stream().mapToLong(Long::longValue).sum();

        return new ReviewStatsResponse(
                Math.round(avg * 10) / 10.0,
                total,
                dist);
    }

    /* ------------------ helpers ------------------ */

    private Review findOwnedReview(Long reviewId, String username) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review","id", reviewId));

        if (!review.getUser().getUsername().equals(username)) {
            throw new IllegalStateException("Not your review.");
        }
        return review;
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser().getUsername(),
                r.getContent(),
                r.getRating(),
                r.getCreatedAt(),
                r.getUpdatedAt(),
                r.getComments()
                        .stream()
                        .map(c -> new ReviewCommentResponse(
                                c.getId(), c.getUser().getUsername(), c.getContent(), c.getCreatedAt()))
                        .toList()
        );
    }
}
