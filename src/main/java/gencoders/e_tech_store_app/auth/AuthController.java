package gencoders.e_tech_store_app.auth;

import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.user.UserRepository;
import gencoders.e_tech_store_app.service.EmailService;
import gencoders.e_tech_store_app.user.UserDetailsImpl;
import gencoders.e_tech_store_app.user.UserService;
import gencoders.e_tech_store_app.user.UserDetailsServiceImpl;
import gencoders.e_tech_store_app.jwt.JwtUtils; // Add this import

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final EmailService emailService;
    private final OTPService otpService;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils; // Add this field

    public AuthController(UserService userService, EmailService emailService, OTPService otpService,
                          AuthenticationManager authenticationManager,
                          UserRepository userRepository, UserDetailsServiceImpl userDetailsService,
                          JwtUtils jwtUtils) { // Add JwtUtils to constructor
        this.userService = userService;
        this.emailService = emailService;
        this.otpService = otpService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils; // Initialize JwtUtils
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
    public ResponseEntity<?> requestOtp(@RequestBody LoginRequest loginRequest, HttpSession session) {
        try {
            if (userService.isLoginValid(loginRequest)) {
                String email = loginRequest.getEmail();
                String otp = otpService.generateOtp(email);
                emailService.sendOtpEmail(email, otp);

                // ✅ Store email in session
                session.setAttribute("OTP_EMAIL", email);

                logger.info("OTP sent to email: {}", email);
                return ResponseEntity.ok(new MessageResponse("OTP sent to your email."));
            } else {
                return ResponseEntity.badRequest().body(new MessageResponse("Invalid credentials."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/signin/resend-otp")
    public ResponseEntity<?> resendOtp(HttpSession session) {
        try {
            String email = (String) session.getAttribute("OTP_EMAIL");

            if (email == null) {
                return ResponseEntity.badRequest().body(new MessageResponse("No OTP process found. Please login again."));
            }

            if (!otpService.hasPendingAuth(email)) {
                return ResponseEntity.badRequest().body(new MessageResponse("No pending authentication found. Please login again."));
            }

            String otp = otpService.resendOtp(email);
            emailService.sendOtpEmail(email, otp);

            logger.info("OTP resent to email: {}", email);
            return ResponseEntity.ok(new MessageResponse("OTP resent to your email."));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error resending OTP: " + e.getMessage()));
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
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);

            // Generate JWT token
            String jwtToken = jwtUtils.generateToken(userDetails);

            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            List<String> roles = userDetailsImpl.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            logger.info("User authenticated successfully: {}", userDetailsImpl.getUsername());

            // Create personalized welcome message
            String welcomeMessage = String.format("Welcome back, %s! Login successful.", userDetailsImpl.getUsername());

            // Return response with JWT token
            return ResponseEntity.ok(new AuthResponse(
                    welcomeMessage,
                    userDetailsImpl.getId(),
                    userDetailsImpl.getUsername(),
                    userDetailsImpl.getEmail(),
                    roles,
                    jwtToken // Add the JWT token here
            ));

        } catch (Exception e) {
            logger.error("Error verifying OTP: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error verifying OTP: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        try {
            if (principal == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new MessageResponse("No authenticated user found."));
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
            UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
            List<String> roles = userDetailsImpl.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new AuthResponse(
                    "User info retrieved successfully",
                    userDetailsImpl.getId(),
                    userDetailsImpl.getUsername(),
                    userDetailsImpl.getEmail(),
                    roles
            ));

        } catch (Exception e) {
            logger.error("Error getting current user: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error retrieving user info: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Principal principal) {
        try {
            String goodbyeMessage = "Logged out successfully.";

            // Get current user's name for personalized goodbye message
            if (principal != null) {
                try {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
                    UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
                    goodbyeMessage = String.format("Goodbye, %s! You have been logged out successfully.", userDetailsImpl.getUsername());
                } catch (Exception e) {
                    logger.warn("Could not retrieve user details for logout message: {}", e.getMessage());
                }
            }

            SecurityContextHolder.clearContext();
            logger.info("User logged out successfully");
            return ResponseEntity.ok(new MessageResponse(goodbyeMessage));
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

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate JWT token
            String jwtToken = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            logger.info("User authenticated successfully: {}", userDetails.getUsername());

            // Create personalized welcome message
            String welcomeMessage = String.format("Welcome back, %s! Login successful.", userDetails.getUsername());

            // Return response with JWT token
            return ResponseEntity.ok(new AuthResponse(
                    welcomeMessage,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles,
                    jwtToken // Add the JWT token here
            ));

        } catch (Exception e) {
            logger.error("Authentication failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid credentials"));
        }
    }
}