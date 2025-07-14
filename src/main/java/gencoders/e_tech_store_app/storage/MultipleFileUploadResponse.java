package gencoders.e_tech_store_app.storage;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class MultipleFileUploadResponse {
    private List<String> urls;
    private List<String> errors;
}