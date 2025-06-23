package gencoders.e_tech_store_app.controller;
import gencoders.e_tech_store_app.dto.LoginRequest;
import gencoders.e_tech_store_app.dto.MessageResponse;
import gencoders.e_tech_store_app.payload.request.SignupRequest;
import gencoders.e_tech_store_app.payload.response.JwtResponse;

import gencoders.e_tech_store_app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    // Constructor injection (better than @Autowired)
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}