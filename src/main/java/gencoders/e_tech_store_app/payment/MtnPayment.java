package gencoders.e_tech_store_app.payment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Getter
@Setter
@Entity
@DiscriminatorValue("MTN_MOMO")
public class MtnPayment extends Payment {
    private String phoneNumber;
    private String rwandaIdNumber;

    @Override
    public boolean processPayment() {
        if (!phoneNumber.startsWith("+250")) {
            throw new IllegalArgumentException("Only Rwandan MTN numbers (+250) accepted");
        }

        boolean success = Math.random() > 0.1; // 90% success rate
        if (success) {
            setTransactionId("MTNRW-" + System.currentTimeMillis());
            return true;
        }
        return false;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        MtnPayment that = (MtnPayment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}