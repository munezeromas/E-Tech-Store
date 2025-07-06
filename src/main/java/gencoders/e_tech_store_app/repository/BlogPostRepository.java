package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Page<BlogPost> findAll(Pageable pageable);

    @Query("SELECT p FROM BlogPost p WHERE p.title LIKE %:query% OR p.content LIKE %:query%")
    List<BlogPost> findByTitleContainingOrContentContaining(String query);

    List<BlogPost> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    List<BlogPost> findByTagsContaining(String tag);

    Optional<BlogPost> findBySlug(String slug);
}