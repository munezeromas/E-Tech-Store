package gencoders.e_tech_store_app.product;

import gencoders.e_tech_store_app.payment.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentProcessedEvent extends ApplicationEvent {
    private final Payment payment;

    public PaymentProcessedEvent(Object source, Payment payment) {
        super(source);
        this.payment = payment;
    }

}