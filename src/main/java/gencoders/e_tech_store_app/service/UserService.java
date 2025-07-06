package gencoders.e_tech_store_app.service;

import gencoders.e_tech_store_app.dto.*;
import gencoders.e_tech_store_app.model.User;
import gencoders.e_tech_store_app.repository.UserRepository;
import gencoders.e_tech_store_app.security.LoginAttemptService;
import gencoders.e_tech_store_app.security.jwt.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;

    @Autowired
    public UserService(AuthenticationManager authenticationManager,
                       LoginAttemptService loginAttemptService,
                       UserRepository userRepository,
                       RoleService roleService,
                       PasswordEncoder encoder,
                       JwtUtils jwtUtils,
                       EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        if (loginAttemptService.isBlocked(loginRequest.getUsername())) {
            throw new RuntimeException("Account temporarily locked due to multiple failed attempts");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            // Clear failed login attempts on successful login
            loginAttemptService.loginSucceeded(loginRequest.getUsername());

            return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles);
        } catch (Exception e) {
            loginAttemptService.loginFailed(loginRequest.getUsername());
            throw e;
        }
    }

    public void registerUser(SignupRequest signUpRequest) {
        validateUserRegistration(signUpRequest);

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Assign default role (USER)
        user.setRoles(roleService.getDefaultRoles());
        userRepository.save(user);
    }

    public void registerAdmin(SignupRequest signUpRequest) {
        validateUserRegistration(signUpRequest);

        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        // Assign admin role
        user.setRoles(roleService.getAdminRoles());
        userRepository.save(user);
    }

    private void validateUserRegistration(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }
    }

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Email not found."));
        }

        String token = jwtUtils.generatePasswordResetToken(user.get().getUsername());
        emailService.sendResetEmail(user.get().getEmail(), token);
        return ResponseEntity.ok(new MessageResponse("Reset link sent to your email."));
    }

    public ResponseEntity<?> resetPassword(PasswordResetRequest request) {
        if (!jwtUtils.validateJwtToken(request.getToken()) || !jwtUtils.isResetTokenValid(request.getToken())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token."));
        }

        String username = jwtUtils.getUserNameFromJwtToken(request.getToken());
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("User not found."));
        }

        user.get().setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user.get());
        return ResponseEntity.ok(new MessageResponse("Password reset successful."));
    }

    public boolean isLoginValid(LoginRequest loginRequest) {
        try {
            // Check if user exists by username or email
            Optional<User> userByUsername = userRepository.findByUsername(loginRequest.getUsername());
            Optional<User> userByEmail = userRepository.findByEmail(loginRequest.getUsername());

            User user = null;
            if (userByUsername.isPresent()) {
                user = userByUsername.get();
            } else if (userByEmail.isPresent()) {
                user = userByEmail.get();
            }

            return user != null && encoder.matches(loginRequest.getPassword(), user.getPassword());
        } catch (Exception e) {
            return false;
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}