// WishlistDto.java (new file)
package gencoders.e_tech_store_app.wishlist;

import lombok.Builder;

import java.util.List;

@Builder
public record WishlistDto(
        List<WishlistItemDto> items,
        int currentPage,
        int totalPages,
        long totalItems,
        String shareLink
) {}