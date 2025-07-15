package gencoders.e_tech_store_app.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResendOTPRequest {
    private String email;
}