package gencoders.e_tech_store_app.auth;

import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.user.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        return ResponseEntity.ok(new MessageResponse("Welcome " + userDetails.getUsername() + "! This is your protected profile."));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        return ResponseEntity.ok(new MessageResponse("Hello " + userDetails.getUsername() + "! This is your dashboard."));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminPanel() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        return ResponseEntity.ok(new MessageResponse("Admin Panel - Welcome " + userDetails.getUsername()));
    }

    @PostMapping("/settings")
    public ResponseEntity<?> updateSettings(@RequestBody String settings) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();

        return ResponseEntity.ok(new MessageResponse("Settings updated for " + userDetails.getUsername()));
    }
}