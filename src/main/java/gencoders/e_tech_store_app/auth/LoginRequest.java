package gencoders.e_tech_store_app.auth;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequest {
    private String firstname;
    private String password;
    private String email;

}
