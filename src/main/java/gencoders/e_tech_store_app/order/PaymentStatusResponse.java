package gencoders.e_tech_store_app.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentStatusResponse {
    private String status;
    private String message;
    private boolean success;
}