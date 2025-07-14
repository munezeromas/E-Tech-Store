package gencoders.e_tech_store_app.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordResetRequest {
    private String token;
    private String newPassword;
    private String confirmPassword;

}