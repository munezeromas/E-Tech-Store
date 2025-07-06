package gencoders.e_tech_store_app.dto;

import lombok.Data;

@Data
public class OTPVerifyRequest {
    private String email;
    private String otp;
}