package gencoders.e_tech_store_app.publiccontrollers;

import gencoders.e_tech_store_app.jwt.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/restricted")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RestrictedAccessController {

    private final JwtUtils jwtUtils;

    public RestrictedAccessController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("/check-auth")
    public ResponseEntity<?> checkAuthStatus(HttpServletRequest request) {
        try {
            String token = jwtUtils.getJwtFromRequest(request);
            if (token != null && jwtUtils.validateJwtToken(token)) {
                return ResponseEntity.ok(new AuthStatusResponse(true, "User is authenticated", "full"));
            } else {
                return ResponseEntity.ok(new AuthStatusResponse(false, "User is not authenticated", "limited"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new AuthStatusResponse(false, "Authentication check failed", "limited"));
        }
    }

    @GetMapping("/protected-actions")
    public ResponseEntity<?> getProtectedActions() {
        return ResponseEntity.ok(new ProtectedActionsResponse(
                List.of(
                        "View Wishlist",
                        "View Cart",
                        "Checkout",
                        "Place Order",
                        "View Order History",
                        "Add/Edit Address",
                        "Write Reviews",
                        "View Profile",
                        "Update Profile"
                )
        ));
    }
}