package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}