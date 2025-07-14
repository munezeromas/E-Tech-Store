package gencoders.e_tech_store_app.shoppingcart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByProductId(Long productId);

}