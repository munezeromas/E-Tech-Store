package gencoders.e_tech_store_app.repository;

import gencoders.e_tech_store_app.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByPostId(Long postId);

    List<Comment> findByAuthorName(String authorName);

    long countByPostId(Long postId);

    void deleteByPostId(Long postId);
}
