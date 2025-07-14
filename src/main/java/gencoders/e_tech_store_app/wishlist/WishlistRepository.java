package gencoders.e_tech_store_app.wishlist;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUserId(String userId);

    boolean existsByUserIdAndProductsId(String userId, Long productId);
    Optional<Wishlist> findByShareToken(String shareToken);

}
