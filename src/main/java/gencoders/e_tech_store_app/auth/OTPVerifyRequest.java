package gencoders.e_tech_store_app.auth;

import lombok.Data;

@Data
public class OTPVerifyRequest {
    private String email;
    private String otp;
}