package gencoders.e_tech_store_app.shoppingcart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('USER')")
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartController(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GetMapping
    public ShoppingCart getCart(@RequestParam Long userId) {
        return shoppingCartService.getCartByUser(userId);
    }

    @PostMapping("/add")
    public ShoppingCart addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity) {
        return shoppingCartService.addItemToCart(userId, productId, quantity);
    }

    @PutMapping("/update")
    public ShoppingCart updateCartItem(
            @RequestParam Long userId,
            @RequestParam Long itemId,
            @RequestParam Integer quantity) {
        return shoppingCartService.updateCartItemQuantity(userId, itemId, quantity);
    }

    @DeleteMapping("/remove")
    public ShoppingCart removeFromCart(
            @RequestParam Long userId,
            @RequestParam Long itemId) {
        return shoppingCartService.removeItemFromCart(userId, itemId);
    }

    @DeleteMapping("/clear")
    public void clearCart(@RequestParam Long userId) {
        shoppingCartService.clearCart(userId);
    }
}