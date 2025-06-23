package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.model.Product;
import gencoders.e_tech_store_app.model.Wishlist;
import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.repository.ProductRepository;
import gencoders.e_tech_store_app.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Wishlist getOrCreateWishlist(String userId) {
        return wishlistRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Wishlist newWishlist = new Wishlist();
                    newWishlist.setUserId(userId);
                    return wishlistRepository.save(newWishlist);
                });
    }

    @Transactional
    public Wishlist addToWishlist(String userId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Wishlist wishlist = getOrCreateWishlist(userId);

        if (!wishlist.containsProduct(product)) {
            wishlist.addProduct(product);
            return wishlistRepository.save(wishlist);
        }
        return wishlist;
    }

    @Transactional
    public void removeFromWishlist(String userId, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));


        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));


        wishlist.removeProduct(product);
        wishlistRepository.save(wishlist);
    }

    public boolean isProductInWishlist(String userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductsId(userId, productId);
    }
}
