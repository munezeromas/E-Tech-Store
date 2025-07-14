package gencoders.e_tech_store_app.address;

import gencoders.e_tech_store_app.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);

    Optional<Address> findByUserAndIsDefaultTrue(User user);

    List<Address> findByUserAndIdNot(User user, Long id);

    int countByUser(User user);
}