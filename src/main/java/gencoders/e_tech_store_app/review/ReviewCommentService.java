// ReviewCommentService.java
package gencoders.e_tech_store_app.review;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.user.User;
import gencoders.e_tech_store_app.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewCommentService {

    private final ReviewRepository reviewRepo;
    private final ReviewCommentRepository commentRepo;
    private final UserRepository userRepo;

    public ReviewCommentResponse addComment(ReviewCommentDto dto, String username) {

        Review review = reviewRepo.findById(dto.reviewId())
                .orElseThrow(() -> new ResourceNotFoundException("Review","id", dto.reviewId()));

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User","username", username));

        ReviewComment comment = new ReviewComment();
        comment.setReview(review);
        comment.setUser(user);
        comment.setContent(dto.content());

        ReviewComment saved = commentRepo.save(comment);
        return new ReviewCommentResponse(
                saved.getId(), user.getUsername(), saved.getContent(), saved.getCreatedAt());
    }
}
