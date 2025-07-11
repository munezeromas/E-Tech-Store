package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserId(String userId);

    boolean existsByUserIdAndProductsId(String userId, Long productId);
}
