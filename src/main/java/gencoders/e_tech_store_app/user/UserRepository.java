// src/main/java/gencoders/e_tech_store_app/user/UserRepository.java
package gencoders.e_tech_store_app.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
    @Override
    Optional<User> findById(Long aLong);
}
