package gencoders.e_tech_store_app.exception;

public class MobileMoneyException extends RuntimeException {
    public MobileMoneyException(String message) {
        super(message);
    }

    public MobileMoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}