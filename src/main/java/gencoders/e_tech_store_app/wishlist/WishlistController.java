package gencoders.e_tech_store_app.wishlist;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class WishlistController {

    private final WishlistService wishlistService;

    /**
     * Get paginated wishlist items for authenticated user
     */
    @GetMapping
    public ResponseEntity<WishlistDto> getWishlist(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(
                wishlistService.getUserWishlist(userDetails.getUsername(), page, size)
        );
    }

    /**
     * Toggle product in wishlist (add if not present, remove if present)
     */
    @PostMapping("/toggle/{productId}")
    public ResponseEntity<WishlistItemDto> toggleWishlistItem(
            @PathVariable String productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                wishlistService.toggleWishlistItem(userDetails.getUsername(), productId)
        );
    }

    /**
     * Get wishlist item count for header badge
     */
    @GetMapping("/count")
    public ResponseEntity<WishlistCountDto> getWishlistItemCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                wishlistService.getWishlistItemCount(userDetails.getUsername())
        );
    }

    /**
     * Remove multiple items from wishlist
     */
    @DeleteMapping("/remove-items")
    public ResponseEntity<Void> removeItemsFromWishlist(
            @RequestBody List<String> productIds,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.removeItemsFromWishlist(userDetails.getUsername(), productIds);
        return ResponseEntity.noContent().build();
    }

    /**
     * Move item from wishlist to cart
     */
    @PostMapping("/move-to-cart/{productId}")
    public ResponseEntity<Void> moveToCart(
            @PathVariable String productId,
            @AuthenticationPrincipal UserDetails userDetails) {
        wishlistService.moveToCart(userDetails.getUsername(), productId);
        return ResponseEntity.ok().build();
    }

    /**
     * Generate shareable link for wishlist
     */
    @PostMapping("/share")
    public ResponseEntity<ShareWishlistResponse> shareWishlist(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(
                wishlistService.generateShareableLink(userDetails.getUsername())
        );
    }

    /**
     * View shared wishlist (public endpoint)
     */
    @GetMapping("/shared/{token}")
    public ResponseEntity<WishlistDto> viewSharedWishlist(
            @PathVariable String token,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(
                wishlistService.getSharedWishlist(token, page, size)
        );
    }
}