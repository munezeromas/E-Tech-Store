package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.ShoppingCart;
import gencoders.e_tech_store_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    Optional<ShoppingCart> findByUser(User user);
}