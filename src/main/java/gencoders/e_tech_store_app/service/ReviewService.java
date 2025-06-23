package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.dto.ReviewDto;
import gencoders.e_tech_store_app.dto.ReviewResponse;
import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.model.Product;
import gencoders.e_tech_store_app.model.Review;
import gencoders.e_tech_store_app.model.User;
import gencoders.e_tech_store_app.repository.ProductRepository;
import gencoders.e_tech_store_app.repository.ReviewRepository;
import gencoders.e_tech_store_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                         ProductRepository productRepository,
                         UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public ReviewResponse addReview(ReviewDto reviewDto, String username) {
        // Validate product exists
        Product product = productRepository.findById(reviewDto.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", reviewDto.getProductId()));

        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Create and save review
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setContent(reviewDto.getContent());
        review.setRating(reviewDto.getRating());
        review.setCreatedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);

        return convertToResponse(savedReview);
    }

    private ReviewResponse convertToResponse(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setAuthor(review.getUser().getUsername());
        response.setContent(review.getContent());
        response.setRating(review.getRating());
        response.setCreatedAt(review.getCreatedAt());
        return response;
    }
}