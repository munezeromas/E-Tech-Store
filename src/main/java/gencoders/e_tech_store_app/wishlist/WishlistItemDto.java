package gencoders.e_tech_store_app.wishlist;

import java.time.LocalDateTime;

public record WishlistItemDto(
        String productId,
        String name,
        String mainImage,
        java.util.Set<String> additionalImages,
        java.math.BigDecimal price,
        java.math.BigDecimal discountedPrice,
        boolean inStock,
        LocalDateTime addedDate,
        boolean isInCart
) {}
