package gencoders.e_tech_store_app.payment.Mtn;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderPaymentResponse {

    @JsonProperty("success")
    private boolean success;

    @JsonProperty("message")
    private String message;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("momoReferenceId")
    private String momoReferenceId;

    @JsonProperty("externalId")
    private String externalId;

    @JsonProperty("paymentStatus")
    private PaymentStatus paymentStatus;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("customerPhone")
    private String customerPhone;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("paymentInitiatedAt")
    private LocalDateTime paymentInitiatedAt;

    @JsonProperty("paymentCompletedAt")
    private LocalDateTime paymentCompletedAt;

    @JsonProperty("momoTransactionId")
    private String momoTransactionId;

    @JsonProperty("errorReason")
    private String errorReason;

    @JsonProperty("nextAction")
    private String nextAction;

    @JsonProperty("estimatedCompletionTime")
    private String estimatedCompletionTime;

    /**
     * Creates a successful payment initiation response
     */
    public static OrderPaymentResponse createSuccessResponse(String orderId, String momoReferenceId,
                                                             String externalId, BigDecimal amount,
                                                             String currency, String customerPhone,
                                                             String customerName) {
        return OrderPaymentResponse.builder()
                .success(true)
                .message("Payment request sent successfully to MTN MoMo")
                .orderId(orderId)
                .momoReferenceId(momoReferenceId)
                .externalId(externalId)
                .paymentStatus(PaymentStatus.PENDING)
                .amount(amount)
                .currency(currency)
                .customerPhone(customerPhone)
                .customerName(customerName)
                .paymentInitiatedAt(LocalDateTime.now())
                .nextAction("Please check your phone for MTN MoMo payment prompt")
                .estimatedCompletionTime("2-3 minutes")
                .build();
    }

    /**
     * Creates a failed payment response
     */
    public static OrderPaymentResponse createFailedResponse(String orderId, String errorReason,
                                                            String customerPhone, String customerName) {
        return OrderPaymentResponse.builder()
                .success(false)
                .message("Payment request failed")
                .orderId(orderId)
                .paymentStatus(PaymentStatus.FAILED)
                .customerPhone(customerPhone)
                .customerName(customerName)
                .errorReason(errorReason)
                .paymentInitiatedAt(LocalDateTime.now())
                .nextAction("Please try again or contact support")
                .build();
    }

    /**
     * Creates a payment status check response
     */
    public static OrderPaymentResponse createStatusResponse(String orderId, String momoReferenceId,
                                                            PaymentStatus status, String momoTransactionId) {
        return OrderPaymentResponse.builder()
                .success(status == PaymentStatus.SUCCESSFUL)
                .message(getStatusMessage(status))
                .orderId(orderId)
                .momoReferenceId(momoReferenceId)
                .paymentStatus(status)
                .momoTransactionId(momoTransactionId)
                .paymentCompletedAt(status == PaymentStatus.SUCCESSFUL ? LocalDateTime.now() : null)
                .nextAction(getNextAction(status))
                .build();
    }

    private static String getStatusMessage(PaymentStatus status) {
        switch (status) {
            case PENDING:
                return "Payment is being processed";
            case SUCCESSFUL:
                return "Payment completed successfully";
            case FAILED:
                return "Payment failed";
            case TIMEOUT:
                return "Payment request timed out";
            case CANCELLED:
                return "Payment was cancelled";
            default:
                return "Unknown payment status";
        }
    }

    private static String getNextAction(PaymentStatus status) {
        switch (status) {
            case PENDING:
                return "Please wait for payment confirmation";
            case SUCCESSFUL:
                return "Your order is confirmed and will be processed";
            case FAILED:
            case TIMEOUT:
            case CANCELLED:
                return "Please try payment again or contact support";
            default:
                return "Check payment status again";
        }
    }
}