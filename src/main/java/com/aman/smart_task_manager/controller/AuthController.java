package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.model.*;
import com.aman.smart_task_manager.repository.UserRepository;
import com.aman.smart_task_manager.security.JwtUtil;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Data static class RegisterRequest { String name, email, password; }
    @Data static class LoginRequest    { String email, password; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByEmail(req.email))
            return ResponseEntity.badRequest().body("Email already in use");
        userRepository.save(User.builder()
                .name(req.name)
                .email(req.email)
                .password(passwordEncoder.encode(req.password))
                .role(Role.USER)
                .build());
        return ResponseEntity.ok("Registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email, req.password));
        return ResponseEntity.ok(Map.of("token", jwtUtil.generateToken(req.email)));
    }
}