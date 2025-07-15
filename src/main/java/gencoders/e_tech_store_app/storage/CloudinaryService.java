package gencoders.e_tech_store_app.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private static final int PROFILE_PICTURE_SIZE = 400;
    private static final int PRODUCT_IMAGE_WIDTH = 800;
    private static final int PRODUCT_IMAGE_HEIGHT = 800;
    private static final String PROFILE_FOLDER = "profile-pictures";
    private static final String PRODUCT_FOLDER = "products";

    public String uploadProfilePicture(MultipartFile file, String username) {
        validateFile(file);
        String folder = PROFILE_FOLDER + "/" + username;

        return doUpload(file, Map.of(
                "folder", folder,
                "transformation", new Transformation()
                        .width(PROFILE_PICTURE_SIZE)
                        .height(PROFILE_PICTURE_SIZE)
                        .crop("fill")
                        .gravity("face")
                        .radius("max")
                        .quality("auto:good")
                        .fetchFormat("auto"),
                "tags", List.of("profile_picture", username)
        ));
    }

    public String uploadProductImage(MultipartFile file, String productId) {
        validateFile(file);
        String folder = PRODUCT_FOLDER + (productId != null ? "/" + productId : "");

        return doUpload(file, Map.of(
                "folder", folder,
                "transformation", new Transformation()
                        .width(PRODUCT_IMAGE_WIDTH)
                        .height(PRODUCT_IMAGE_HEIGHT)
                        .crop("pad")
                        .background("white")
                        .quality("auto:good")
                        .fetchFormat("auto"),
                "tags", productId != null ? List.of("product_image", productId) : List.of("product_image")
        ));
    }

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);
        return doUpload(file, Map.of(
                "folder", folder,
                "quality", "auto:good",
                "fetch_format", "auto"
        ));
    }

    public String uploadWithTransformation(MultipartFile file, String folder, int width, int height) {
        validateFile(file);

        return doUpload(file, Map.of(
                "folder", folder,
                "transformation", new Transformation()
                        .width(width)
                        .height(height)
                        .crop("fill")
                        .gravity("face")
                        .quality("auto:good")
                        .fetchFormat("auto")
        ));
    }

    public void deleteFile(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                String publicId = extractPublicIdFromUrl(imageUrl);
                if (publicId != null) {
                    Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    log.info("File deleted successfully: {} (Result: {})", publicId, result.get("result"));
                }
            }
        } catch (IOException ex) {
            log.error("Failed to delete image: {}", imageUrl, ex);
            throw new RuntimeException("Failed to delete image: " + ex.getMessage(), ex);
        }
    }

    public List<String> getUserProfilePictures(String username) {
        try {
            String folder = PROFILE_FOLDER + "/" + username;
            Map<?, ?> result = cloudinary.search()
                    .expression("folder:" + folder)
                    .sortBy("created_at", "desc")
                    .maxResults(10)
                    .execute();

            List<Map<?, ?>> resources = (List<Map<?, ?>>) result.get("resources");
            List<String> urls = new ArrayList<>();
            for (Map<?, ?> resource : resources) {
                urls.add(resource.get("secure_url").toString());
            }

            return urls;
        } catch (Exception e) {
            log.error("Failed to get profile pictures for user: {}", username, e);
            return new ArrayList<>();
        }
    }

    public List<String> getProductImages(String productId) {
        try {
            String folder = PRODUCT_FOLDER + "/" + productId;
            Map<?, ?> result = cloudinary.search()
                    .expression("folder:" + folder)
                    .sortBy("created_at", "desc")
                    .maxResults(20)
                    .execute();

            List<Map<?, ?>> resources = (List<Map<?, ?>>) result.get("resources");
            List<String> urls = new ArrayList<>();
            for (Map<?, ?> resource : resources) {
                urls.add(resource.get("secure_url").toString());
            }

            return urls;
        } catch (Exception e) {
            log.error("Failed to get product images for product: {}", productId, e);
            return new ArrayList<>();
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            String base = imageUrl.substring(imageUrl.indexOf("/upload/") + 8);
            base = base.replaceAll("^v\\d+/", "");
            return FilenameUtils.removeExtension(base);
        } catch (Exception e) {
            log.warn("Could not extract public ID from URL: {}", imageUrl);
            return null;
        }
    }

    private String doUpload(MultipartFile file, Map<String, Object> options) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
            return result.get("secure_url").toString();  // Cloudinary will handle the signature and timestamp internally
        } catch (IOException ex) {
            log.error("Upload failed for file: {}", file.getOriginalFilename(), ex);
            throw new RuntimeException("Upload failed: " + ex.getMessage(), ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("File size cannot exceed 10MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        List<String> allowed = List.of("image/jpeg", "image/png", "image/gif", "image/webp");
        if (!allowed.contains(contentType)) {
            throw new IllegalArgumentException("Only JPEG, PNG, GIF, and WebP images are allowed");
        }
    }
    // Add this method to your CloudinaryService class
    public Map<String, Object> testConnection() throws Exception {
        return cloudinary.api().ping(new HashMap<>());
    }
}
