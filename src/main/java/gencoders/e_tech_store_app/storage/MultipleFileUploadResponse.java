package gencoders.e_tech_store_app.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for multiple file upload
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultipleFileUploadResponse {
    private List<String> urls;
    private List<String> errors;
    private String message;
    private LocalDateTime timestamp;

    public MultipleFileUploadResponse(List<String> urls, List<String> errors) {
        this.urls = urls;
        this.errors = errors;
        this.message = String.format("Uploaded %d files successfully", urls.size());
        if (!errors.isEmpty()) {
            this.message += String.format(" with %d errors", errors.size());
        }
        this.timestamp = LocalDateTime.now();
    }
}