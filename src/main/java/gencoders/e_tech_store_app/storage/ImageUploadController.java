package gencoders.e_tech_store_app.storage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for uploading, managing, and deleting images in Cloudinary.
 * Supports both profile pictures and product images with different access controls.
 */
@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "File Upload", description = "Endpoints for uploading and managing files in cloud storage")
public class ImageUploadController {

    private final CloudinaryService cloudinaryService;

    /* --------------------------------------------------------------------
     * 1. Upload Profile Picture (Authenticated users can upload their own)
     * ------------------------------------------------------------------ */
    @PostMapping(value = "/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Upload profile picture",
            description = "Upload a profile picture for the authenticated user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile picture uploaded successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProfilePictureResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file format or size"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized – Authentication required"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during upload"
                    )
            }
    )
    public ResponseEntity<?> uploadProfilePicture(
            @Parameter(description = "Profile picture file to upload", required = true)
            @RequestPart("file") MultipartFile file) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("User {} uploading profile picture", username);

            String url = cloudinaryService.uploadProfilePicture(file, username);
            return ResponseEntity.ok(new ProfilePictureResponse(url, username));
        } catch (IllegalArgumentException e) {
            log.error("Invalid file for profile picture upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid file: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload profile picture", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Upload failed: " + e.getMessage()));
        }
    }

    /* --------------------------------------------------------------------
     * 2. Upload a single product image (Admin only)
     * ------------------------------------------------------------------ */
    @PostMapping(value = "/product-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Upload product image",
            description = "Upload a single product image with optimized settings for e-commerce",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product image uploaded successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductImageResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file format or size"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized – Admin role required"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during upload"
                    )
            }
    )
    public ResponseEntity<?> uploadProductImage(
            @Parameter(description = "Product image file", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Product ID for organization", required = false)
            @RequestParam(required = false) String productId) {

        try {
            log.info("Admin uploading product image for product: {}", productId);
            String url = cloudinaryService.uploadProductImage(file, productId);
            return ResponseEntity.ok(new ProductImageResponse(url, productId));
        } catch (IllegalArgumentException e) {
            log.error("Invalid file for product image upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid file: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload product image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Upload failed: " + e.getMessage()));
        }
    }

    /* --------------------------------------------------------------------
     * 3. Upload multiple product images (Admin only)
     * ------------------------------------------------------------------ */
    @PostMapping(value = "/product-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Upload multiple product images",
            description = "Upload multiple product images in a single request",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product images uploaded with results",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MultipleFileUploadResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<MultipleFileUploadResponse> uploadMultipleProductImages(
            @Parameter(description = "Array of product image files", required = true)
            @RequestPart("files") List<MultipartFile> files,
            @Parameter(description = "Product ID for organization", required = false)
            @RequestParam(required = false) String productId) {

        log.info("Admin uploading {} product images for product: {}", files.size(), productId);

        List<String> urls = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String url = cloudinaryService.uploadProductImage(file, productId);
                urls.add(url);
                log.info("Successfully uploaded: {}", file.getOriginalFilename());
            } catch (IllegalArgumentException e) {
                String error = "Invalid file " + file.getOriginalFilename() + ": " + e.getMessage();
                errors.add(error);
                log.error(error);
            } catch (Exception e) {
                String error = "Failed to upload " + file.getOriginalFilename() + ": " + e.getMessage();
                errors.add(error);
                log.error(error, e);
            }
        }

        return ResponseEntity.ok(new MultipleFileUploadResponse(urls, errors));
    }

    /* --------------------------------------------------------------------
     * 4. Generic image upload (Admin only)
     * ------------------------------------------------------------------ */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Upload generic image",
            description = "Upload a generic image file to cloud storage",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image uploaded successfully",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = FileUploadResponse.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> uploadImage(
            @Parameter(description = "Image file to upload", required = true)
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "Target folder for organization", schema = @Schema(defaultValue = "general"))
            @RequestParam(defaultValue = "general") String folder) {

        try {
            log.info("Admin uploading generic image to folder: {}", folder);
            String url = cloudinaryService.uploadFile(file, folder);
            return ResponseEntity.ok(new FileUploadResponse(url));
        } catch (IllegalArgumentException e) {
            log.error("Invalid file for generic image upload: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid file: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Failed to upload generic image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Upload failed: " + e.getMessage()));
        }
    }

    /* --------------------------------------------------------------------
     * 5. Delete an image by its URL (Admin only)
     * ------------------------------------------------------------------ */
    @DeleteMapping("/image")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete image",
            description = "Delete an image from cloud storage by its URL",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Image deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Image not found"
                    )
            }
    )
    public ResponseEntity<?> deleteImage(
            @Parameter(description = "URL of the image to delete", required = true)
            @RequestParam String imageUrl) {

        try {
            log.info("Admin deleting image: {}", imageUrl);
            cloudinaryService.deleteFile(imageUrl);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Failed to delete image: {}", imageUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Delete failed: " + e.getMessage()));
        }
    }

    /* --------------------------------------------------------------------
     * 6. Delete profile picture (Users can delete their own)
     * ------------------------------------------------------------------ */
    @DeleteMapping("/profile-picture")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Delete profile picture",
            description = "Delete the authenticated user's profile picture",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Profile picture deleted successfully"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - Cannot delete other user's profile picture"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Profile picture not found"
                    )
            }
    )
    public ResponseEntity<?> deleteProfilePicture(
            @Parameter(description = "URL of the profile picture to delete", required = true)
            @RequestParam String imageUrl) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            log.info("User {} deleting profile picture: {}", username, imageUrl);

            if (imageUrl.contains("/profile-pictures/" + username + "/")) {
                cloudinaryService.deleteFile(imageUrl);
                return ResponseEntity.noContent().build();
            } else {
                log.warn("User {} attempted to delete profile picture that doesn't belong to them", username);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ErrorResponse("Cannot delete other user's profile picture"));
            }
        } catch (Exception e) {
            log.error("Failed to delete profile picture: {}", imageUrl, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Delete failed: " + e.getMessage()));
        }
    }

    /* --------------------------------------------------------------------
     * 7. Get user's profile pictures (Users can view their own)
     * ------------------------------------------------------------------ */
    @GetMapping("/profile-pictures")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(
            summary = "Get user's profile pictures",
            description = "Retrieve all profile pictures for the authenticated user"
    )
    public ResponseEntity<List<String>> getUserProfilePictures() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("User {} retrieving profile pictures", username);

        List<String> urls = cloudinaryService.getUserProfilePictures(username);
        return ResponseEntity.ok(urls);
    }

    /* --------------------------------------------------------------------
     * 8. Get product images (Admin only)
     * ------------------------------------------------------------------ */
    @GetMapping("/product-images")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get product images",
            description = "Retrieve all images for a specific product"
    )
    public ResponseEntity<List<String>> getProductImages(
            @Parameter(description = "Product ID", required = true)
            @RequestParam String productId) {

        log.info("Admin retrieving images for product: {}", productId);
        List<String> urls = cloudinaryService.getProductImages(productId);
        return ResponseEntity.ok(urls);
    }

    /* --------------------------------------------------------------------
     * 9. Test Cloudinary connection
     * ------------------------------------------------------------------ */
    @GetMapping("/test-connection")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Test Cloudinary connection")
    public ResponseEntity<?> testCloudinaryConnection() {
        try {
            Map<String, Object> result = cloudinaryService.testConnection();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Cloudinary connection successful",
                    "timestamp", LocalDateTime.now(),
                    "result", result
            ));
        } catch (Exception e) {
            log.error("Cloudinary connection test failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "status", "error",
                            "message", "Cloudinary connection failed: " + e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }

    /* --------------------------------------------------------------------
     * Error Response DTO
     * ------------------------------------------------------------------ */
    @Data
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private LocalDateTime timestamp;

        public ErrorResponse(String error) {
            this.error = error;
            this.timestamp = LocalDateTime.now();
        }
    }
}