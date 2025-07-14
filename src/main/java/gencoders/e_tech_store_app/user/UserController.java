package gencoders.e_tech_store_app.user;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.config.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Important import for file handling

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    private final UserService userService; // Inject the UserService

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // Existing endpoint, can be used by admins to fetch any user by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')") // Only mods/admins can view arbitrary user profiles
    public User getUserById(@PathVariable(value = "id") Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * Endpoint for the currently authenticated user to view their own profile.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public User getMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userService.getMyProfile(userDetails);
    }

    /**
     * Endpoint for the currently authenticated user to update their own profile (text fields).
     */
    @PutMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> updateMyProfile(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                             @RequestBody User updatedUser) {
        userService.updateMyProfile(userDetails, updatedUser);
        return ResponseEntity.ok(new MessageResponse("Profile updated successfully!"));
    }

    /**
     * Endpoint for the currently authenticated user to upload/update their profile picture.
     * The @RequestParam("file") annotation maps the "file" part of the multipart request
     * to the MultipartFile object.
     */
    @PostMapping("/me/profile-picture")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<?> uploadProfilePicture(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                  @RequestParam("file") MultipartFile file) { // Expects a file named "file"
        userService.updateProfilePicture(userDetails, file);
        return ResponseEntity.ok(new MessageResponse("Profile picture updated successfully!"));
    }

    /**
     * Endpoint for viewing a public profile of any user (e.g., a seller's profile).
     * This endpoint should be accessible to all, even unauthenticated users, to allow
     * browsing seller information before login/purchase.
     */
    @GetMapping("/public/{userId}")
    // No @PreAuthorize here, as it should be publicly accessible.
    // Ensure your Spring Security configuration allows this endpoint without authentication.
    public User getPublicUserProfile(@PathVariable(value = "userId") Long userId) {
        return userService.getPublicUserProfile(userId);
    }
}
