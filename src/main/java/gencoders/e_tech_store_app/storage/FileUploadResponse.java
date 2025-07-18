package gencoders.e_tech_store_app.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for single file upload
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponse {
    private String url;
    private String message;
    private LocalDateTime timestamp;

    public FileUploadResponse(String url) {
        this.url = url;
        this.message = "File uploaded successfully";
        this.timestamp = LocalDateTime.now();
    }
}