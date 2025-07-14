package gencoders.e_tech_store_app.wishlist;

import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.product.Product;
import gencoders.e_tech_store_app.product.ProductRepository;
import gencoders.e_tech_store_app.shoppingcart.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ShoppingCartService cartService;

    @Transactional(readOnly = true)
    public WishlistDto getUserWishlist(String userId, int page, int size) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> createNewWishlist(userId));

        List<Product> products = wishlist.getProducts().stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) wishlist.getProducts().size() / size);

        return WishlistDto.builder()
                .items(products.stream()
                        .map(this::mapToWishlistItemDto)
                        .collect(Collectors.toList()))
                .currentPage(page)
                .totalPages(totalPages)
                .totalItems(wishlist.getProducts().size())
                .shareLink(generateShareableLink(userId).shareLink())
                .build();
    }

    @Transactional
    public WishlistItemDto toggleWishlistItem(String userId, String productIdStr) {
        Long productId = Long.parseLong(productIdStr);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> createNewWishlist(userId));

        boolean isInWishlist = wishlist.getProducts().contains(product);

        if (isInWishlist) {
            wishlist.removeProduct(product);
        } else {
            wishlist.addProduct(product);
        }

        wishlistRepository.save(wishlist);

        return mapToWishlistItemDto(product);
    }

    @Transactional(readOnly = true)
    public WishlistCountDto getWishlistItemCount(String userId) {
        int count = wishlistRepository.findByUserId(userId)
                .map(w -> w.getProducts().size())
                .orElse(0);
        return new WishlistCountDto(count);
    }

    @Transactional
    public void removeItemsFromWishlist(String userId, List<String> productIdStrings) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));

        for (String idStr : productIdStrings) {
            Long id = Long.parseLong(idStr);
            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            wishlist.removeProduct(product);
        }

        wishlistRepository.save(wishlist);
    }

    @Transactional
    public void moveToCart(String userId, String productIdStr) {
        Long productId = Long.parseLong(productIdStr);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        wishlist.removeProduct(product);
        wishlistRepository.save(wishlist);

        cartService.addItemToCart(Long.parseLong(userId), productId, 1);
    }

    @Transactional(readOnly = true)
    public WishlistDto getSharedWishlist(String token, int page, int size) {
        Wishlist wishlist = wishlistRepository.findByShareToken(token)
                .filter(w -> w.getShareTokenExpiry().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired share token"));

        List<Product> products = wishlist.getProducts().stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());

        int totalPages = (int) Math.ceil((double) wishlist.getProducts().size() / size);

        return WishlistDto.builder()
                .items(products.stream()
                        .map(this::mapToWishlistItemDto)
                        .collect(Collectors.toList()))
                .currentPage(page)
                .totalPages(totalPages)
                .totalItems(wishlist.getProducts().size())
                .shareLink(null)
                .build();
    }

    @Transactional
    public ShareWishlistResponse generateShareableLink(String userId) {
        Wishlist wishlist = wishlistRepository.findByUserId(userId)
                .orElseGet(() -> createNewWishlist(userId));

        String token = UUID.randomUUID().toString();
        wishlist.setShareToken(token);
        wishlist.setShareTokenExpiry(LocalDateTime.now().plusDays(7));
        wishlistRepository.save(wishlist);

        return new ShareWishlistResponse(
                "/api/wishlist/shared/" + token,
                wishlist.getShareTokenExpiry()
        );
    }

    private Wishlist createNewWishlist(String userId) {
        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        return wishlistRepository.save(wishlist);
    }

    private WishlistItemDto mapToWishlistItemDto(Product product) {
        boolean inCart = cartService.isProductInCart(product.getId());

        return new WishlistItemDto(
                product.getId().toString(),
                product.getName(),
                product.getImageUrl(),
                product.getAdditionalImages(),
                product.getPrice(),
                product.getDiscountedPrice(),
                product.getStockQuantity() > 0,
                LocalDateTime.now(),
                inCart
        );
    }
}
