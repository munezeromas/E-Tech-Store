package gencoders.e_tech_store_app.wishlist;

import java.time.LocalDateTime;

public record ShareWishlistResponse(
        String shareLink,
        LocalDateTime expiryDate
) {}
