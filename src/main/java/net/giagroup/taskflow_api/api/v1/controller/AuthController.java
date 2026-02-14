package net.giagroup.taskflow_api.api.v1.controller;

import net.giagroup.taskflow_api.api.v1.dto.AuthRequest;
import net.giagroup.taskflow_api.api.v1.dto.AuthResponse;
import net.giagroup.taskflow_api.api.v1.dto.RegisterRequest;
import net.giagroup.taskflow_api.domain.User;
import net.giagroup.taskflow_api.repository.UserRepository;
import net.giagroup.taskflow_api.security.JwtProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @org.springframework.beans.factory.annotation.Value("${jwt.expiration-ms}")
    private long expirationMs;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String token = jwtProvider.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", expirationMs));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User u = User.builder()
                .username(req.username())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .build();
        userRepository.save(u);
        return ResponseEntity.created(URI.create("/api/v1/users/" + u.getId())).build();
    }
}
