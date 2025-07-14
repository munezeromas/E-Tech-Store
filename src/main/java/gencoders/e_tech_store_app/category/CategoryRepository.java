package gencoders.e_tech_store_app.category;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findBySlug(String slug);
    @EntityGraph(attributePaths = {"products"}) // Explicitly control loading
    List<Category> findAll();
    @EntityGraph(attributePaths = {"products.specifications"})
    Optional<Category> findById(Long id);
}

