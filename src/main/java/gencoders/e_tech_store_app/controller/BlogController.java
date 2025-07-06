package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.dto.*;
import gencoders.e_tech_store_app.service.BlogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BlogController {

    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public ResponseEntity<Page<BlogPostDTO>> getAllPosts(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(blogService.getAllPosts(pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogPostDTO> createPost(@Valid @RequestBody BlogPostRequest request) {
        BlogPostDTO createdPost = blogService.createPost(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(createdPost.getId()).toUri();
        return ResponseEntity.created(location).body(createdPost);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BlogPostDTO> updatePost(@PathVariable Long id,
                                                  @Valid @RequestBody BlogPostRequest request) {
        return ResponseEntity.ok(blogService.updatePost(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/comments")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CommentDTO> addComment(@PathVariable Long postId,
                                                 @Valid @RequestBody CommentRequest request) {
        return ResponseEntity.ok(blogService.addComment(postId, request));
    }

    @GetMapping("/search")
    public ResponseEntity<List<BlogPostDTO>> searchPosts(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(blogService.searchPosts(query));
    }

    @GetMapping("/search/date")
    public ResponseEntity<List<BlogPostDTO>> searchPostsByDateRange(@RequestParam LocalDate start,
                                                                    @RequestParam LocalDate end) {
        return ResponseEntity.ok(blogService.searchPostsByDateRange(start, end));
    }

    @GetMapping("/search/tag")
    public ResponseEntity<List<BlogPostDTO>> searchPostsByTag(@RequestParam String tag) {
        return ResponseEntity.ok(blogService.searchPostsByTag(tag));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<BlogPostDTO> getPostBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogService.getPostBySlug(slug));
    }
}
