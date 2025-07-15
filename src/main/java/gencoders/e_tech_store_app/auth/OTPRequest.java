package gencoders.e_tech_store_app.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPRequest {
    private String email;
    private String otp;

}
