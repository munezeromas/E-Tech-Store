package gencoders.e_tech_store_app.payment.Mtn;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

//import javax.validation.constraints.*;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentRequest {

    @NotNull(message = "Order ID is required")
    @NotBlank(message = "Order ID cannot be blank")
    @JsonProperty("orderId")
    private String orderId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @DecimalMin(value = "100.0", message = "Minimum payment amount is 100 RWF")
    @JsonProperty("amount")
    private BigDecimal amount;

    @NotNull(message = "Currency is required")
    @Pattern(regexp = "^(RWF|USD|EUR)$", message = "Currency must be RWF, USD, or EUR")
    @JsonProperty("currency")
    private String currency;

    @NotNull(message = "Customer phone number is required")
    @Pattern(regexp = "^25078[0-9]{7}$", message = "Phone number must be valid Rwanda format (25078XXXXXXX)")
    @JsonProperty("customerPhone")
    private String customerPhone;

    @NotNull(message = "Customer name is required")
    @NotBlank(message = "Customer name cannot be blank")
    @Size(min = 2, max = 50, message = "Customer name must be between 2 and 50 characters")
    @JsonProperty("customerName")
    private String customerName;

    @Email(message = "Customer email must be valid")
    @JsonProperty("customerEmail")
    private String customerEmail;

    @JsonProperty("paymentDescription")
    private String paymentDescription;

    @JsonProperty("orderItems")
    private String orderItems; // JSON string of order items

    /**
     * Generates payment description for MoMo
     */
    public String getPayerMessage() {
        return String.format("Payment for Order #%s by %s", orderId, customerName);
    }

    /**
     * Generates admin note for MoMo
     */
    public String getPayeeNote() {
        return String.format("E-Tech Store - Order #%s - %s RWF", orderId, amount);
    }

    /**
     * Generates unique external ID for MoMo
     */
    public String getExternalId() {
        return "ETECH-" + orderId + "-" + System.currentTimeMillis();
    }

    /**
     * Validates if all required fields are present
     */
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty() &&
                amount != null && amount.compareTo(BigDecimal.ZERO) > 0 &&
                currency != null && !currency.trim().isEmpty() &&
                customerPhone != null && !customerPhone.trim().isEmpty() &&
                customerName != null && !customerName.trim().isEmpty();
    }
}