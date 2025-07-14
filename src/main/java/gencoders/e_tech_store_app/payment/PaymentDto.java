package gencoders.e_tech_store_app.payment;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentDto {
    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;

    // For MTN Mobile Money
    private String phoneNumber;

    // For Credit Cards
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
}