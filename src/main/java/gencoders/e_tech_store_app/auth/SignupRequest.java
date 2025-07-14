package gencoders.e_tech_store_app.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatch  // Custom annotation applied here
public class SignupRequest {

        @NotBlank(message = "Email is required.")
        @Valid
        @Size(max = 50, message = "Email should not exceed 50 characters.")
        @Email(message = "Please provide a valid email address.")
        private String email;

    @NotBlank(message = "First name is required.")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters.")
    private String lastName;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, max = 40, message = "Password should be between 6 and 40 characters.")
    private String password;

    @NotBlank(message = "Password confirmation is required.")
    @Size(min = 6, max = 40, message = "Password confirmation should be between 6 and 40 characters.")
    private String confirmPassword;

}