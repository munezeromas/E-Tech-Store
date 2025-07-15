package gencoders.e_tech_store_app.address;

import gencoders.e_tech_store_app.order.Order;
import gencoders.e_tech_store_app.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Street is required")
    @Size(max = 100, message = "Street must be less than 100 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must be less than 50 characters")
    private String city;

    @NotBlank(message = "District is required")
    @Size(max = 50, message = "State must be less than 50 characters")
    private String District;

    @NotBlank(message = "Province is required")
    @Size(max = 50, message = "State must be less than 50 characters")
    private String Province;

    @NotBlank(message = "Description is required")
    @Size(max = 50, message = "State must be less than 50 characters")
    private String Description;


    @NotBlank(message = "Zip code is required")
    @Size(max = 20, message = "Zip code must be less than 20 characters")
    private String zipCode;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must be less than 50 characters")
    private String country;

    @Size(max = 20, message = "Phone must be less than 20 characters")
    private String phone;

    @Column(name = "is_default")
    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "shippingAddress", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Order> orders = new HashSet<>();

    public Address() {
    }

    public Address(String street, String city, String state, String zipCode, String country, User user) {
        this.street = street;
        this.city = city;
        this.District = state;
        this.Province = state;
        this.Description = state;
        this.zipCode = zipCode;
        this.country = country;
        this.user = user;
    }

    // Helper method to get full address
    public String getFullAddress() {
        return String.format("%s, %s, , %s ,%s %s, %s, %s",street, city, District,Description,Province, zipCode, country);
    }
}