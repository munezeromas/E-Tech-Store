package gencoders.e_tech_store_app.auth;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthResponse {
    private String message;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
    private String jwt;
    private String tokenType = "Bearer";

    public AuthResponse(String message, Long id, String username, String email, List<String> roles) {
        this.message = message;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public AuthResponse(String message, Long id, String username, String email, List<String> roles, String jwt) {
        this.message = message;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.jwt = jwt;
    }
}