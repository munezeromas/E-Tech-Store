package gencoders.e_tech_store_app.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String zipCode;
    private String description;
    private String district;
    private String province;
    private String country;
    private String phone;
    private boolean isDefault;
    private Long userId;
}
