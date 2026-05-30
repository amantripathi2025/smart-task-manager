package com.aman.smart_task_manager.controller;

import com.aman.smart_task_manager.dto.request.LoginRequest;
import com.aman.smart_task_manager.dto.request.RegisterRequest;
import com.aman.smart_task_manager.dto.response.AuthResponse;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
