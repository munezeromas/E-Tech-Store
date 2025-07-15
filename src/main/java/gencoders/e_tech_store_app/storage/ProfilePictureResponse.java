package gencoders.e_tech_store_app.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for profile picture upload
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilePictureResponse {
    private String url;
    private String username;
    private String message;
    private LocalDateTime timestamp;

    public ProfilePictureResponse(String url, String username) {
        this.url = url;
        this.username = username;
        this.message = "Profile picture uploaded successfully";
        this.timestamp = LocalDateTime.now();
    }
}