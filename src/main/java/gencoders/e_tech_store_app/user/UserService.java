package gencoders.e_tech_store_app.user;

import gencoders.e_tech_store_app.auth.*;
import gencoders.e_tech_store_app.config.MessageResponse;
import gencoders.e_tech_store_app.exception.ResourceNotFoundException;
import gencoders.e_tech_store_app.jwt.JwtResponse;
import gencoders.e_tech_store_app.jwt.JwtUtils;
import gencoders.e_tech_store_app.role.RoleService;
import gencoders.e_tech_store_app.storage.CloudinaryService;
import gencoders.e_tech_store_app.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public UserService(AuthenticationManager authenticationManager,
                       LoginAttemptService loginAttemptService,
                       UserRepository userRepository,
                       RoleService roleService,
                       PasswordEncoder encoder,
                       JwtUtils jwtUtils,
                       EmailService emailService,
                       CloudinaryService cloudinaryService) {
        this.authenticationManager = authenticationManager;
        this.loginAttemptService = loginAttemptService;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
    }

    /* -------------  authentication / registration ------------- */

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        if (loginAttemptService.isBlocked(loginRequest.getFirstname())) {
            throw new RuntimeException("Account temporarily locked due to multiple failed attempts");
        }

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getFirstname(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(auth);
            String jwt = jwtUtils.generateJwtToken(auth);

            UserDetailsImpl userDetails = (UserDetailsImpl) auth.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            loginAttemptService.loginSucceeded(loginRequest.getFirstname());

            return new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles);
        } catch (Exception ex) {
            loginAttemptService.loginFailed(loginRequest.getFirstname());
            throw ex;
        }
    }

    public void registerUser(SignupRequest req) { createUser(req, false); }

    public void registerAdmin(SignupRequest req) { createUser(req, true); }

    private void createUser(SignupRequest req, boolean admin) {
        validateUserRegistration(req);

        User user = new User(
                req.getFirstName(),
                req.getEmail(),
                encoder.encode(req.getPassword()),
                req.getFirstName(),
                req.getLastName());

        user.setRoles(admin ? roleService.getAdminRoles()
                : roleService.getDefaultRoles());
        userRepository.save(user);
    }

    private void validateUserRegistration(SignupRequest req) {
        if (userRepository.existsByUsername(req.getFirstName()))
            throw new RuntimeException("Error: Username is already taken!");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("Error: Email is already in use!");
    }

    /* -------------  password reset ------------- */

    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest req) {
        Optional<User> user = userRepository.findByEmail(req.getEmail());
        if (user.isEmpty())
            return ResponseEntity.badRequest().body(new MessageResponse("Email not found."));

        String token = jwtUtils.generatePasswordResetToken(user.get().getUsername());
        emailService.sendResetEmail(user.get().getEmail(), token);
        return ResponseEntity.ok(new MessageResponse("Reset link sent to your email."));
    }

    public ResponseEntity<?> resetPassword(PasswordResetRequest req) {
        if (!jwtUtils.validateJwtToken(req.getToken()) ||
                !jwtUtils.isResetTokenValid(req.getToken())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Invalid or expired token."));
        }

        String username = jwtUtils.getUserNameFromJwtToken(req.getToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        user.setPassword(encoder.encode(req.getNewPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("Password reset successful."));
    }

    /* -------------  login helper ------------- */

    public boolean isLoginValid(LoginRequest loginRequest) {
        Optional<User> byUser = userRepository.findByUsername(loginRequest.getFirstname());
        Optional<User> byMail = userRepository.findByEmail(loginRequest.getFirstname());

        // --- FIXED: remove bad method reference ---
        User user = byUser.orElse(byMail.orElse(null));

        return user != null && encoder.matches(loginRequest.getPassword(), user.getPassword());
    }

    /* -------------  profile operations ------------- */

    public User getMyProfile(UserDetailsImpl details) {
        return userRepository.findById(details.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", details.getId()));
    }

    public User updateMyProfile(UserDetailsImpl details, User patch) {
        User user = getMyProfile(details);

        if (patch.getUsername()     != null) user.setUsername(patch.getUsername());
        if (patch.getEmail()        != null) user.setEmail(patch.getEmail());
        if (patch.getFirstName()    != null) user.setFirstName(patch.getFirstName());
        if (patch.getLastName()     != null) user.setLastName(patch.getLastName());
        if (patch.getPhoneNumber()  != null) user.setPhoneNumber(patch.getPhoneNumber());
        if (patch.getDescription()  != null) user.setDescription(patch.getDescription());

        return userRepository.save(user);
    }

    public void updateProfilePicture(UserDetailsImpl details, MultipartFile file) {
        User user = getMyProfile(details);

        if (user.getProfilePictureUrl() != null) {
            try { cloudinaryService.deleteFile(user.getProfilePictureUrl()); }
            catch (Exception ex) {
                log.warn("Failed to delete old profile picture: {}", ex.getMessage());
            }
        }

        String newUrl = cloudinaryService.uploadWithTransformation(
                file, "profile-pictures", 300, 300);
        user.setProfilePictureUrl(newUrl);
        userRepository.save(user);

        log.info("Profile picture updated for user {}", user.getUsername());
    }

    public User getPublicUserProfile(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        User pub = new User();
        pub.setId(u.getId());
        pub.setUsername(u.getUsername());
        pub.setFirstName(u.getFirstName());
        pub.setLastName(u.getLastName());
        pub.setProfilePictureUrl(u.getProfilePictureUrl());
        pub.setDescription(u.getDescription());
        pub.setRoles(u.getRoles());
        return pub;
    }
}
