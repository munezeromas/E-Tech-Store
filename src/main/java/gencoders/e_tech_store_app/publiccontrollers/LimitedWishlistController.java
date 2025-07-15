package gencoders.e_tech_store_app.publiccontrollers;

import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.jwt.JwtUtils;
import gencoders.e_tech_store_app.wishlist.WishlistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LimitedWishlistController {

    private final WishlistService wishlistService;
    private final JwtUtils jwtUtils;

    public LimitedWishlistController(WishlistService wishlistService, JwtUtils jwtUtils) {
        this.wishlistService = wishlistService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/add/{productId}")
    public ResponseEntity<?> addToWishlist(@PathVariable Long productId, HttpServletRequest request) {
        try {
            String token = jwtUtils.getJwtFromRequest(request);

            if (token != null && jwtUtils.validateJwtToken(token)) {
                // User is authenticated - add to their wishlist
                String username = jwtUtils.getUserNameFromJwtToken(token);
                wishlistService.addToWishlist(username, productId);
                return ResponseEntity.ok(new MessageResponse("Product added to wishlist successfully!"));
            } else {
                // User is not authenticated - store in session or return message
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Please login to add items to your wishlist. You can view products but need to authenticate for wishlist access."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error adding to wishlist: " + e.getMessage()));
        }
    }

    @GetMapping("/view")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> viewWishlist(Principal principal) {
        try {
            // Only authenticated users can view their wishlist
            return ResponseEntity.ok(wishlistService.getUserWishlist(principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching wishlist: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFromWishlist(@PathVariable Long productId, Principal principal) {
        try {
            // Only authenticated users can remove items from wishlist
            wishlistService.removeFromWishlist(principal.getName(), productId);
            return ResponseEntity.ok(new MessageResponse("Product removed from wishlist successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error removing from wishlist: " + e.getMessage()));
        }
    }
}

