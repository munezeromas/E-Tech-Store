package gencoders.e_tech_store_app.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String folder) {
        validateFile(file);

        try {
            String publicId = generatePublicId(folder);
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    "resource_type", "auto",
                    "quality", "auto:good",
                    "fetch_format", "auto"
            );

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String url = result.get("secure_url").toString();
            log.info("File uploaded successfully to: {}", url);
            return url;

        } catch (IOException ex) {
            log.error("Image upload failed for file: {}", file.getOriginalFilename(), ex);
            throw new RuntimeException("Image upload failed: " + ex.getMessage(), ex);
        }
    }

    public String uploadWithTransformation(MultipartFile file, String folder, int width, int height) {
        validateFile(file);

        try {
            String publicId = generatePublicId(folder);
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", publicId,
                    "transformation", new Object[] {
                            ObjectUtils.asMap(
                                    "width", width,
                                    "height", height,
                                    "crop", "fill",
                                    "gravity", "face",
                                    "quality", "auto:good",
                                    "fetch_format", "auto"
                            )
                    }
            );

            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            String url = result.get("secure_url").toString();
            log.info("File uploaded with transformation to: {}", url);
            return url;

        } catch (IOException ex) {
            log.error("Image upload with transformation failed", ex);
            throw new RuntimeException("Image upload failed: " + ex.getMessage(), ex);
        }
    }

    public void deleteFile(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                String publicId = extractPublicIdFromUrl(imageUrl);
                if (publicId != null) {
                    cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                    log.info("File deleted successfully: {}", publicId);
                }
            }
        } catch (IOException ex) {
            log.error("Failed to delete image: {}", imageUrl, ex);
            throw new RuntimeException("Failed to delete image: " + ex.getMessage(), ex);
        }
    }

    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            if (parts.length >= 7) {
                StringBuilder publicId = new StringBuilder();
                for (int i = 7; i < parts.length; i++) {
                    if (i > 7) publicId.append("/");
                    publicId.append(parts[i]);
                }
                String result = publicId.toString();
                int lastDot = result.lastIndexOf('.');
                if (lastDot > 0) {
                    result = result.substring(0, lastDot);
                }
                return result;
            }
        } catch (Exception e) {
            log.warn("Could not extract public ID from URL: {}", imageUrl);
        }
        return null;
    }

    private String generatePublicId(String folder) {
        return folder + "_" + UUID.randomUUID().toString().replace("-", "");
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

        String[] allowedTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};
        boolean isAllowed = Arrays.asList(allowedTypes).contains(contentType);

        if (!isAllowed) {
            throw new IllegalArgumentException("Only JPEG, PNG, GIF, and WebP images are allowed");
        }
    }
}