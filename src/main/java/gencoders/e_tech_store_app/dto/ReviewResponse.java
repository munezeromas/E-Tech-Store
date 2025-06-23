package gencoders.e_tech_store_app.dto;

import java.time.LocalDateTime;

// ReviewResponse.java
public class ReviewResponse {
    private Long id;
    private String author;
    private String content;
    private int rating;
    private LocalDateTime createdAt;

    // Getters
    public Long getId() { return id; }
    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public int getRating() { return rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setAuthor(String author) { this.author = author; }
    public void setContent(String content) { this.content = content; }
    public void setRating(int rating) { this.rating = rating; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
