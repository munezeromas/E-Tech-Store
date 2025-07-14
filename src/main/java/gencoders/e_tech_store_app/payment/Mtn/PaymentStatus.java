package gencoders.e_tech_store_app.payment.Mtn;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * Enum representing different payment statuses in the MTN MoMo payment flow
 */
public enum PaymentStatus {

    /**
     * Payment request has been initiated but not yet completed
     */
    PENDING("PENDING", "Payment is being processed"),

    /**
     * Payment has been successfully completed
     */
    SUCCESSFUL("SUCCESSFUL", "Payment completed successfully"),

    /**
     * Payment has failed due to various reasons
     */
    FAILED("FAILED", "Payment failed"),

    /**
     * Payment request has timed out
     */
    TIMEOUT("TIMEOUT", "Payment request timed out"),

    /**
     * Payment was cancelled by the user
     */
    CANCELLED("CANCELLED", "Payment was cancelled"),

    /**
     * Payment is being processed by MTN MoMo
     */
    PROCESSING("PROCESSING", "Payment is being processed by MTN MoMo"),

    /**
     * Payment was rejected by the system
     */
    REJECTED("REJECTED", "Payment was rejected"),

    /**
     * Payment status is unknown
     */
    UNKNOWN("UNKNOWN", "Payment status is unknown");

    private final String status;
    @Getter
    private final String description;

    PaymentStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    @JsonValue
    public String getStatus() {
        return status;
    }

    /**
     * Converts MTN MoMo API status to our PaymentStatus enum
     */
    public static PaymentStatus fromMomoStatus(String momoStatus) {
        if (momoStatus == null || momoStatus.trim().isEmpty()) {
            return UNKNOWN;
        }

        switch (momoStatus.toUpperCase()) {
            case "PENDING":
                return PENDING;
            case "SUCCESSFUL":
            case "SUCCESS":
                return SUCCESSFUL;
            case "FAILED":
            case "FAILURE":
                return FAILED;
            case "TIMEOUT":
                return TIMEOUT;
            case "CANCELLED":
            case "CANCELED":
                return CANCELLED;
            case "PROCESSING":
                return PROCESSING;
            case "REJECTED":
                return REJECTED;
            default:
                return UNKNOWN;
        }
    }

    /**
     * Checks if the payment status is final (completed, failed, etc.)
     */
    public boolean isFinal() {
        return this == SUCCESSFUL || this == FAILED || this == TIMEOUT ||
                this == CANCELLED || this == REJECTED;
    }

    /**
     * Checks if the payment status indicates success
     */
    public boolean isSuccessful() {
        return this == SUCCESSFUL;
    }

    /**
     * Checks if the payment status indicates failure
     */
    public boolean isFailure() {
        return this == FAILED || this == TIMEOUT || this == CANCELLED || this == REJECTED;
    }

    /**
     * Checks if the payment is still in progress
     */
    public boolean isInProgress() {
        return this == PENDING || this == PROCESSING;
    }

    @Override
    public String toString() {
        return status;
    }
}