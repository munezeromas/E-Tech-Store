package gencoders.e_tech_store_app.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@DiscriminatorValue("CREDIT_CARD")
public class CreditCardPayment extends Payment {
    private String encryptedCardNumber;
    private String cardHolderName;
    private String expiryDate;
    private String cvv;
    private String billingAddress;

    public void setCardNumber(String cardNumber) {
        this.encryptedCardNumber = encrypt(cardNumber);
    }

    @Override
    public boolean processPayment() {
        // TODO: Integrate with actual payment gateway
        boolean success = Math.random() > 0.2; // 80% success rate (placeholder)
        if (success) {
            setTransactionId("CC" + System.currentTimeMillis());
            return true;
        }
        return false;
    }

    private String encrypt(String data) {
        // TODO: Implement encryption (e.g., using Spring Security or a payment gateway token)
        return data; // Placeholder
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CreditCardPayment that = (CreditCardPayment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}