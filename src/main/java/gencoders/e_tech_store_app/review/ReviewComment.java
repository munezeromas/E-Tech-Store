// ReviewComment.java
package gencoders.e_tech_store_app.review;

import gencoders.e_tech_store_app.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(name = "review_comments")
public class ReviewComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /* relations */
    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)  @JoinColumn(name = "user_id")
    private User user;
}
