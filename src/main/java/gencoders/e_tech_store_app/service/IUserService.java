package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.dto.LoginRequest;
import gencoders.e_tech_store_app.payload.request.ForgotPasswordRequest;
import gencoders.e_tech_store_app.payload.request.ResetPasswordRequest;
import gencoders.e_tech_store_app.dto.SignupRequest;
import gencoders.e_tech_store_app.dto.JwtResponse;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    JwtResponse authenticateUser(LoginRequest loginRequest);
    void registerUser(SignupRequest signUpRequest);
    ResponseEntity<?> forgotPassword(ForgotPasswordRequest request);
    ResponseEntity<?> resetPassword(ResetPasswordRequest request);
}