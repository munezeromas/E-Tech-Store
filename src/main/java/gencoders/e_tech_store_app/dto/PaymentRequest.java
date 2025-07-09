package gencoders.e_tech_store_app.dto;

import gencoders.e_tech_store_app.model.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long orderId;
    private PaymentMethod paymentMethod;
    private BigDecimal amount;
    private String currencyCode;
    private String countryCode;

    // Credit card fields
    private String cardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;

    // Mobile money fields
    private String mobileNumber;
    private String mobileMoneyProvider; // "MTN" or "AIRTEL"

    // Customer information
    private String customerEmail;
}