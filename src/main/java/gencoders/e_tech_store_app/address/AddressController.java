package gencoders.e_tech_store_app.address;

import gencoders.e_tech_store_app.user.User;
import gencoders.e_tech_store_app.user.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')") // All methods in this controller require USER role
public class AddressController {
    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<Address>> getUserAddresses(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // User ID derived from authenticated principal
        User user = userDetails.getUser();
        return ResponseEntity.ok(addressService.getUserAddresses(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Address> getAddressById(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Ensure the address belongs to the authenticated user
        return ResponseEntity.ok(addressService.getAddressByIdAndUser(id, userDetails.getUser()));
    }

    @GetMapping("/default")
    public ResponseEntity<Address> getDefaultAddress(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(addressService.getDefaultAddress(user));
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@Valid @RequestBody AddressRequest request,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(addressService.createAddress(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long id,
                                                 @Valid @RequestBody AddressRequest request,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Ensure the address being updated belongs to the authenticated user
        return ResponseEntity.ok(addressService.updateAddress(id, request, userDetails.getUser()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Ensure the address being deleted belongs to the authenticated user
        addressService.deleteAddress(id, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/default")
    public ResponseEntity<Address> setDefaultAddress(@PathVariable Long id,
                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails.getUser();
        return ResponseEntity.ok(addressService.setDefaultAddress(id, user));
    }
}
