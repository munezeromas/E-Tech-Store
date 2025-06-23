package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
    Optional<Double> findAverageRatingByProductId(Long productId);
}
