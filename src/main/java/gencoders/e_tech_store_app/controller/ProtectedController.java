package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.auth.RoleAuthorizationUtil;
import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.jwt.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/protected")
public class ProtectedController {

    private final RoleAuthorizationUtil roleAuthUtil;
    private final JwtUtils jwtUtils;

    public ProtectedController(RoleAuthorizationUtil roleAuthUtil, JwtUtils jwtUtils) {
        this.roleAuthUtil = roleAuthUtil;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Endpoint accessible only by ADMIN users
     */
    @GetMapping("/admin-only")
    public ResponseEntity<?> adminOnlyEndpoint(@RequestHeader("Authorization") String authHeader) {
        if (!roleAuthUtil.isValidAdminToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Access denied. Admin privileges required."));
        }

        String username = roleAuthUtil.getUsername(authHeader);
        return ResponseEntity.ok(new MessageResponse("Hello Admin " + username + "! This is admin-only content."));
    }

    /**
     * Endpoint accessible by both USER and ADMIN
     */
    @GetMapping("/user-content")
    public ResponseEntity<?> userContentEndpoint(@RequestHeader("Authorization") String authHeader) {
        if (!roleAuthUtil.isValidUserToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Access denied. User authentication required."));
        }

        String username = roleAuthUtil.getUsername(authHeader);
        String userType = roleAuthUtil.getUserType(authHeader);

        return ResponseEntity.ok(new MessageResponse(
                "Hello " + username + "! You are accessing this as a " + userType + " user."));
    }

    /**
     * Endpoint that shows different content based on user role
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboardEndpoint(@RequestHeader("Authorization") String authHeader) {
        if (!roleAuthUtil.isValidUserToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Access denied. Authentication required."));
        }

        String username = roleAuthUtil.getUsername(authHeader);

        if (roleAuthUtil.isAdminUser(authHeader)) {
            return ResponseEntity.ok(new MessageResponse(
                    "Welcome to Admin Dashboard, " + username + "! You have full system access."));
        } else {
            return ResponseEntity.ok(new MessageResponse(
                    "Welcome to User Dashboard, " + username + "! You have limited access."));
        }
    }

    /**
     * Endpoint to get current user's token information
     */
    @GetMapping("/token-info")
    public ResponseEntity<?> getTokenInfo(@RequestHeader("Authorization") String authHeader) {
        String token = roleAuthUtil.extractTokenFromHeader(authHeader);
        if (token == null || !jwtUtils.validateJwtToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid or missing token."));
        }

        Map<String, Object> tokenInfo = jwtUtils.getTokenInfo(token);
        return ResponseEntity.ok(tokenInfo);
    }

    /**
     * Endpoint for role-specific operations
     */
    @PostMapping("/role-specific-action")
    public ResponseEntity<?> roleSpecificAction(@RequestHeader("Authorization") String authHeader,
                                                @RequestParam String action) {

        if (!roleAuthUtil.isValidUserToken(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Access denied. Authentication required."));
        }

        String username = roleAuthUtil.getUsername(authHeader);

        switch (action.toLowerCase()) {
            case "delete":
                if (!roleAuthUtil.isAdminUser(authHeader)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new MessageResponse("Only admins can perform delete operations."));
                }
                return ResponseEntity.ok(new MessageResponse("Delete operation performed by admin: " + username));

            case "read":
                return ResponseEntity.ok(new MessageResponse("Read operation performed by: " + username));

            case "update":
                if (!roleAuthUtil.isAdminUser(authHeader)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(new MessageResponse("Only admins can perform update operations."));
                }
                return ResponseEntity.ok(new MessageResponse("Update operation performed by admin: " + username));

            default:
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Invalid action. Supported actions: read, update, delete"));
        }
    }
}