package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.model.*;
import gencoders.e_tech_store_app.repository.CartItemRepository;
import gencoders.e_tech_store_app.repository.ProductRepository;
import gencoders.e_tech_store_app.repository.ShoppingCartRepository;
import gencoders.e_tech_store_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class ShoppingCartService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public ShoppingCart getCartByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return shoppingCartRepository.findByUser(user)
                .orElseGet(() -> {
                    ShoppingCart newCart = new ShoppingCart();
                    newCart.setUser(user);
                    return shoppingCartRepository.save(newCart);
                });
    }

    public ShoppingCart addItemToCart(Long userId, Long productId, Integer quantity) {
        ShoppingCart cart = getCartByUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem(product, quantity);
            newItem.setCart(cart);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.calculateTotalPrice();
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart updateCartItemQuantity(Long userId, Long itemId, Integer quantity) {
        ShoppingCart cart = getCartByUser(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        cart.calculateTotalPrice();
        return shoppingCartRepository.save(cart);
    }

    public ShoppingCart removeItemFromCart(Long userId, Long itemId) {
        ShoppingCart cart = getCartByUser(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("Item does not belong to user's cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        cart.calculateTotalPrice();
        return shoppingCartRepository.save(cart);
    }

    public void clearCart(Long userId) {
        ShoppingCart cart = getCartByUser(userId);
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        shoppingCartRepository.save(cart);
    }
}