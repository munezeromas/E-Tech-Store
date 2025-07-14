package gencoders.e_tech_store_app.payment;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RwandaPaymentDto extends PaymentDto {
    private String nationalId;
    private String customerName;
}