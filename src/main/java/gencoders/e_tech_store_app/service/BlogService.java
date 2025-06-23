package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.model.BlogPost;
import gencoders.e_tech_store_app.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository blogRepository;

    public List<BlogPost> getAllPosts() {
        return blogRepository.findAllByOrderByCreatedAtDesc();
    }

    public BlogPost getPostById(Long id) {
        return blogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog not found", "id",blogRepository.findById(id)));

    }
}