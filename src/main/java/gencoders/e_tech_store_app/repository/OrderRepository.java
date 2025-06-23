package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Order;
import gencoders.e_tech_store_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}