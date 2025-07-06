package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.dto.*;
import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.model.BlogPost;
import gencoders.e_tech_store_app.model.Comment;
import gencoders.e_tech_store_app.repository.BlogPostRepository;
import gencoders.e_tech_store_app.repository.CommentRepository;
import gencoders.e_tech_store_app.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final BlogPostRepository blogPostRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Override
    public Page<BlogPostDTO> getAllPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable)
                .map(post -> modelMapper.map(post, BlogPostDTO.class));
    }

    @Override
    @Transactional
    public BlogPostDTO createPost(BlogPostRequest request) {
        BlogPost post = modelMapper.map(request, BlogPost.class);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        BlogPost savedPost = blogPostRepository.save(post);
        return modelMapper.map(savedPost, BlogPostDTO.class);
    }

    @Override
    public BlogPostDTO getPostById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return modelMapper.map(post, BlogPostDTO.class);
    }

    @Override
    @Transactional
    public BlogPostDTO updatePost(Long id, BlogPostRequest request) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));

        modelMapper.map(request, post);
        post.setUpdatedAt(LocalDateTime.now());
        BlogPost updatedPost = blogPostRepository.save(post);
        return modelMapper.map(updatedPost, BlogPostDTO.class);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        blogPostRepository.delete(post);
    }

    @Override
    @Transactional
    public CommentDTO addComment(Long postId, CommentRequest request) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

        Comment comment = modelMapper.map(request, Comment.class);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        CommentDTO commentDTO = modelMapper.map(savedComment, CommentDTO.class);
        commentDTO.setPostId(postId);
        return commentDTO;
    }

    @Override
    public List<BlogPostDTO> searchPosts(String query) {
        return blogPostRepository.findByTitleContainingOrContentContaining(query)
                .stream()
                .map(post -> modelMapper.map(post, BlogPostDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogPostDTO> searchPostsByDateRange(LocalDate start, LocalDate end) {
        return blogPostRepository.findByCreatedAtBetween(
                        start.atStartOfDay(), end.plusDays(1).atStartOfDay())
                .stream()
                .map(post -> modelMapper.map(post, BlogPostDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<BlogPostDTO> searchPostsByTag(String tag) {
        return blogPostRepository.findByTagsContaining(tag)
                .stream()
                .map(post -> modelMapper.map(post, BlogPostDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BlogPostDTO getPostBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with slug: " + slug));
        return modelMapper.map(post, BlogPostDTO.class);
    }
}