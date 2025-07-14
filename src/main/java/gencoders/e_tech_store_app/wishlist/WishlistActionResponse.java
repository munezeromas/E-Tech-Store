package gencoders.e_tech_store_app.wishlist;

public record WishlistActionResponse(
        String message,
        int updatedItemCount,
        boolean isInWishlist
) {}
