package gencoders.e_tech_store_app.auth;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator to check if password and confirmPassword match.
 */
public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, SignupRequest> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        // Initialization logic if needed.
    }

    @Override
    public boolean isValid(SignupRequest signupRequest, ConstraintValidatorContext context) {
        if (signupRequest == null) {
            return true; // No need to validate if the object is null.
        }
        return signupRequest.getPassword().equals(signupRequest.getConfirmPassword());
    }
}
