package gencoders.e_tech_store_app.dto;

import gencoders.e_tech_store_app.model.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long paymentId;
    private String transactionId;
    private PaymentStatus status;
    private BigDecimal amount;
    private String currencyCode;
    private String message;
    private LocalDateTime paymentDate;
    private String gatewayResponse;
    private String paymentMethod;

    // Payment method specific fields
    private String maskedCardNumber;
    private String mobileMoneyReference;
    private String bankTransferReference;
    private String receiptUrl;  // Added for PayPal receipts

    // Conversion fields
    private BigDecimal convertedAmount;
    private String baseCurrencyCode;

    // Additional information
    private String customerId;
    private String countryCode;
    private boolean requiresAction;
    private String actionUrl;
}