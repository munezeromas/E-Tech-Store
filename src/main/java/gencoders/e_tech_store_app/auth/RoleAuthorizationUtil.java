package gencoders.e_tech_store_app.auth;

import gencoders.e_tech_store_app.jwt.JwtUtils;
import org.springframework.stereotype.Component;

@Component
public class RoleAuthorizationUtil {

    private final JwtUtils jwtUtils;

    public RoleAuthorizationUtil(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * Extract JWT token from Authorization header
     */
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Check if the token belongs to an admin user
     */
    public boolean isAdminUser(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return token != null && jwtUtils.canAccessAdminResources(token);
    }

    /**
     * Check if the token belongs to a regular user (or admin)
     */
    public boolean isRegularUser(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return token != null && jwtUtils.canAccessUserResources(token);
    }

    /**
     * Check if the token has a specific role
     */
    public boolean hasRole(String authHeader, String role) {
        String token = extractTokenFromHeader(authHeader);
        return token != null && jwtUtils.hasRole(token, role);
    }

    /**
     * Get user type from token
     */
    public String getUserType(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return token != null ? jwtUtils.getUserTypeFromToken(token) : "UNKNOWN";
    }

    /**
     * Get username from token
     */
    public String getUsername(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return token != null ? jwtUtils.getUserNameFromJwtToken(token) : null;
    }

    /**
     * Validate if token is valid and belongs to admin
     */
    public boolean isValidAdminToken(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return token != null && jwtUtils.validateJwtToken(token) && jwtUtils.isAdminToken(token);
    }

    /**
     * Validate if token is valid and belongs to user (including admin)
     */
    public boolean isValidUserToken(String authHeader) {
        String token = extractTokenFromHeader(authHeader);
        return token != null && jwtUtils.validateJwtToken(token) &&
                (jwtUtils.isUserToken(token) || jwtUtils.isAdminToken(token));
    }
}