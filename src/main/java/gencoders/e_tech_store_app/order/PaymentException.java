package gencoders.e_tech_store_app.order;

public class PaymentException extends RuntimeException {
    public PaymentException(String message) {
        super(message);
    }
}