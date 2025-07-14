package gencoders.e_tech_store_app.order;

import gencoders.e_tech_store_app.address.Address;
import gencoders.e_tech_store_app.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Table(name = "orders")
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> items = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal shippingFee;
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;

    // Rwanda-specific fields
    private String rwandaTaxCode;
    private String rwandaPhoneNumber;

    public void calculateTotals() {
        this.subtotal = items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.tax = subtotal.multiply(new BigDecimal("0.18")); // 18% VAT in Rwanda
        this.shippingFee = new BigDecimal("2000"); // 2000 RWF shipping fee
        this.total = subtotal.add(tax).add(shippingFee);
    }
}