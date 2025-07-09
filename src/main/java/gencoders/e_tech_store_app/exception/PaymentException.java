package gencoders.e_tech_store_app.exception;

import lombok.Getter;

/**
 * Custom exception for payment-related errors
 */
@Getter
public class PaymentException extends RuntimeException {

    private final String errorCode;
    private final Object[] parameters;

    public PaymentException(String message) {
        super(message);
        this.errorCode = null;
        this.parameters = null;
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
        this.parameters = null;
    }

    public PaymentException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = null;
    }

    public PaymentException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = null;
    }

    public PaymentException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public PaymentException(String errorCode, String message, Throwable cause, Object... parameters) {
        super(message, cause);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    // Common payment error codes
    public static final String PAYMENT_NOT_FOUND = "PAYMENT_NOT_FOUND";
    public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
    public static final String INVALID_PAYMENT_METHOD = "INVALID_PAYMENT_METHOD";
    public static final String INVALID_AMOUNT = "INVALID_AMOUNT";
    public static final String PAYMENT_ALREADY_PROCESSED = "PAYMENT_ALREADY_PROCESSED";
    public static final String PAYMENT_CANNOT_BE_REFUNDED = "PAYMENT_CANNOT_BE_REFUNDED";
    public static final String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
    public static final String CARD_DECLINED = "CARD_DECLINED";
    public static final String EXPIRED_CARD = "EXPIRED_CARD";
    public static final String INVALID_CARD = "INVALID_CARD";
    public static final String GATEWAY_ERROR = "GATEWAY_ERROR";
    public static final String NETWORK_ERROR = "NETWORK_ERROR";
    public static final String TIMEOUT_ERROR = "TIMEOUT_ERROR";
    public static final String DUPLICATE_TRANSACTION = "DUPLICATE_TRANSACTION";
    public static final String CURRENCY_NOT_SUPPORTED = "CURRENCY_NOT_SUPPORTED";

    // Factory methods for common exceptions
    public static PaymentException paymentNotFound(String transactionId) {
        return new PaymentException(PAYMENT_NOT_FOUND,
                "Payment not found with transaction ID: " + transactionId, transactionId);
    }

    public static PaymentException orderNotFound(Long orderId) {
        return new PaymentException(ORDER_NOT_FOUND,
                "Order not found with ID: " + orderId, orderId);
    }

    public static PaymentException invalidPaymentMethod(String method) {
        return new PaymentException(INVALID_PAYMENT_METHOD,
                "Invalid payment method: " + method, method);
    }

    public static PaymentException invalidAmount(String amount) {
        return new PaymentException(INVALID_AMOUNT,
                "Invalid amount: " + amount, amount);
    }

    public static PaymentException paymentAlreadyProcessed(String transactionId) {
        return new PaymentException(PAYMENT_ALREADY_PROCESSED,
                "Payment already processed for transaction ID: " + transactionId, transactionId);
    }

    public static PaymentException cannotBeRefunded(String transactionId, String reason) {
        return new PaymentException(PAYMENT_CANNOT_BE_REFUNDED,
                "Payment cannot be refunded for transaction ID: " + transactionId + ". Reason: " + reason,
                transactionId, reason);
    }

    public static PaymentException cardDeclined(String lastFourDigits) {
        return new PaymentException(CARD_DECLINED,
                "Card ending in " + lastFourDigits + " was declined", lastFourDigits);
    }

    public static PaymentException expiredCard(String lastFourDigits) {
        return new PaymentException(EXPIRED_CARD,
                "Card ending in " + lastFourDigits + " has expired", lastFourDigits);
    }

    public static PaymentException invalidCard(String lastFourDigits) {
        return new PaymentException(INVALID_CARD,
                "Invalid card ending in " + lastFourDigits, lastFourDigits);
    }

    public static PaymentException gatewayError(String gateway, String errorMessage) {
        return new PaymentException(GATEWAY_ERROR,
                "Gateway error from " + gateway + ": " + errorMessage, gateway, errorMessage);
    }

    public static PaymentException networkError(String message) {
        return new PaymentException(NETWORK_ERROR,
                "Network error: " + message, message);
    }

    public static PaymentException timeoutError(String operation) {
        return new PaymentException(TIMEOUT_ERROR,
                "Timeout error during " + operation, operation);
    }

    public static PaymentException duplicateTransaction(String transactionId) {
        return new PaymentException(DUPLICATE_TRANSACTION,
                "Duplicate transaction detected: " + transactionId, transactionId);
    }

    public static PaymentException currencyNotSupported(String currency) {
        return new PaymentException(CURRENCY_NOT_SUPPORTED,
                "Currency not supported: " + currency, currency);
    }
}