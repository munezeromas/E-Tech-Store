package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.ERole;
import gencoders.e_tech_store_app.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(ERole name);
    boolean existsByName(ERole name);
}
