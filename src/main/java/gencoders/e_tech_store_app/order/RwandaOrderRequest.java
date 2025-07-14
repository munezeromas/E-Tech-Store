package gencoders.e_tech_store_app.order;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RwandaOrderRequest extends OrderRequest {
    private String phoneNumber; // Format: +25078xxxxxx
    private String taxCode;    // Rwanda Revenue Authority code
    private String nationalId; // National ID for verification
}