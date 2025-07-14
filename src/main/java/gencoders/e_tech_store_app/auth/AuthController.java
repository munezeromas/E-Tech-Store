package gencoders.e_tech_store_app.auth;

import gencoders.e_tech_store_app.jwt.JwtResponse;
import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.user.UserRepository;
import gencoders.e_tech_store_app.jwt.JwtUtils;
import gencoders.e_tech_store_app.service.EmailService;
import gencoders.e_tech_store_app.user.UserDetailsImpl;
import gencoders.e_tech_store_app.user.UserService;
import gencoders.e_tech_store_app.user.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final EmailService emailService;
    private final OTPService otpService;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthController(UserService userService, EmailService emailService, OTPService otpService, AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, UserDetailsServiceImpl userDetailsService) {
        this.userService = userService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Password and Confirm Password do not match."));
        }
        try {
            userService.registerUser(signUpRequest);
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/signup/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody SignupRequest signUpRequest) {
        if (!signUpRequest.getPassword().equals(signUpRequest.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Password and Confirm Password do not match."));
        }
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

            if (!otpService.validateOtp(request.getEmail(), request.getOtp())) {
                logger.warn("Invalid or expired OTP for email: {}", request.getEmail());
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired OTP."));
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            String jwt = jwtUtils.generateJwtToken(authToken);

            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            List<String> roles = userDetailsImpl.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            logger.info("User authenticated successfully: {}", userDetailsImpl.getUsername());

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
