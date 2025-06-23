package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Address;
import gencoders.e_tech_store_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
}