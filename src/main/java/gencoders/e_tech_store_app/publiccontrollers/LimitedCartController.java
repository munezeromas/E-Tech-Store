package gencoders.e_tech_store_app.publiccontrollers;

import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.jwt.JwtUtils;
import gencoders.e_tech_store_app.shoppingcart.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class LimitedCartController {

    private final ShoppingCartService cartService;
    private final JwtUtils jwtUtils;

    public LimitedCartController(ShoppingCartService cartService, JwtUtils jwtUtils) {
        this.cartService = cartService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/add-authenticated")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest request, HttpServletRequest httpRequest) {
        try {
            String token = jwtUtils.getJwtFromRequest(httpRequest);

            if (token != null && jwtUtils.validateJwtToken(token)) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                cartService.addToCart(username, request.getProductId(), request.getQuantity());
                return ResponseEntity.ok(new MessageResponse("Product added to cart successfully!"));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("Please login to add items to your cart. You can view products but need to authenticate for cart access."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error adding to cart: " + e.getMessage()));
        }
    }

    @GetMapping("/view")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> viewCart(Principal principal) {
        try {
            return ResponseEntity.ok(cartService.getUserCart(principal.getName()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error fetching cart: " + e.getMessage()));
        }
    }

    @PutMapping("/update/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateCartItem(@PathVariable Long itemId, @RequestBody CartUpdateRequest request, Principal principal) {
        try {
            cartService.updateCartItem(principal.getName(), itemId, request.getQuantity());
            return ResponseEntity.ok(new MessageResponse("Cart item updated successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error updating cart item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/remove/{itemId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> removeFromCart(@PathVariable Long itemId, Principal principal) {
        try {
            cartService.removeFromCart(principal.getName(), itemId);
            return ResponseEntity.ok(new MessageResponse("Product removed from cart successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error removing from cart: " + e.getMessage()));
        }
    }
}
