package gencoders.e_tech_store_app.controller;

import gencoders.e_tech_store_app.dto.*;
import gencoders.e_tech_store_app.model.User;
import gencoders.e_tech_store_app.repository.UserRepository;
import gencoders.e_tech_store_app.security.jwt.JwtUtils;
import gencoders.e_tech_store_app.service.EmailService;
import gencoders.e_tech_store_app.service.OTPService;
import gencoders.e_tech_store_app.service.UserDetailsImpl;
import gencoders.e_tech_store_app.service.UserService;
import gencoders.e_tech_store_app.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private OTPService otpService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            userService.registerUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody SignupRequest signUpRequest) {
        try {
            userService.registerAdmin(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("Admin registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/signin/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody LoginRequest loginRequest) {
        try {
            if (userService.isLoginValid(loginRequest)) {
                String otp = otpService.generateOtp(loginRequest.getEmail());
                emailService.sendOtpEmail(loginRequest.getEmail(), otp);
                logger.info("OTP sent to email: {}", loginRequest.getEmail());
                return ResponseEntity.ok(new MessageResponse("OTP sent to your email."));
            } else {
                logger.warn("Invalid credentials for email: {}", loginRequest.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid credentials."));
            }
        } catch (Exception e) {
            logger.error("Error sending OTP: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/signin/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OTPRequest request) {
        try {
            logger.info("Verifying OTP for email: {}", request.getEmail());

            // 1. Validate OTP first
            if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
                logger.warn("Invalid or expired OTP for email: {}", request.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired OTP."));
            }

            // 2. Load user details using UserDetailsService
            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(request.getEmail());
            } catch (Exception e) {
                logger.error("User not found for email: {}", request.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("User not found."));
            }

            // 3. Create authentication token without password verification
            // Since OTP is already verified, we trust the user's identity
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // 4. Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // 5. Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authToken);

            // 6. Cast to UserDetailsImpl to get additional info
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            List<String> roles = userDetailsImpl.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            logger.info("User authenticated successfully: {}", userDetailsImpl.getUsername());

            // 7. Return JWT response
            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetailsImpl.getId(),
                    userDetailsImpl.getUsername(),
                    userDetailsImpl.getEmail(),
                    roles));

        } catch (Exception e) {
            logger.error("Error verifying OTP: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error verifying OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            Principal principal = request.getUserPrincipal();
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("No user is currently logged in."));
            }

            String username = principal.getName();
            SecurityContextHolder.clearContext();

            logger.info("User logged out successfully: {}", username);
            return ResponseEntity.ok(new MessageResponse("User " + username + " logged out successfully."));
        } catch (Exception e) {
            logger.error("Error during logout: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error during logout: " + e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        return userService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Passwords do not match."));
        }
        return userService.resetPassword(request);
    }
}