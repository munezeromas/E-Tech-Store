package gencoders.e_tech_store_app.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    private final CloudinaryService cloudinaryService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "products") String folder) {

        log.info("Admin uploading image to folder: {}", folder);
        String url = cloudinaryService.uploadFile(file, folder);
        return ResponseEntity.ok(new FileUploadResponse(url));
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MultipleFileUploadResponse> uploadMultipleImages(
            @RequestPart("files") List<MultipartFile> files,
            @RequestParam(defaultValue = "products") String folder) {

        log.info("Admin uploading {} images to folder: {}", files.size(), folder);

        List<String> urls = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String url = cloudinaryService.uploadFile(file, folder);
                urls.add(url);
            } catch (Exception e) {
                errors.add("Failed to upload " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        return ResponseEntity.ok(new MultipleFileUploadResponse(urls, errors));
    }

    @PostMapping(value = "/product-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadProductImage(
            @RequestPart("file") MultipartFile file,
            @RequestParam(defaultValue = "products") String folder) {

        log.info("Admin uploading product image to folder: {}", folder);
        String url = cloudinaryService.uploadFile(file, folder);
        return ResponseEntity.ok(new FileUploadResponse(url));
    }

    @DeleteMapping("/image")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteImage(@RequestParam String imageUrl) {
        log.info("Admin deleting image: {}", imageUrl);
        cloudinaryService.deleteFile(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
