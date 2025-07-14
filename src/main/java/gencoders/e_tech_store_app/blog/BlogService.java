package gencoders.e_tech_store_app.blog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface BlogService {
    Page<BlogPostDTO> getAllPosts(Pageable pageable);
    BlogPostDTO createPost(BlogPostRequest request);
    BlogPostDTO getPostById(Long id);
    BlogPostDTO updatePost(Long id, BlogPostRequest request);
    void deletePost(Long id);
    CommentDTO addComment(Long postId, CommentRequest request);
    List<BlogPostDTO> searchPosts(String query);
    List<BlogPostDTO> searchPostsByDateRange(LocalDate start, LocalDate end);
    List<BlogPostDTO> searchPostsByTag(String tag);
    BlogPostDTO getPostBySlug(String slug);
}