package com.aman.smart_task_manager.service;

import com.aman.smart_task_manager.dto.request.LoginRequest;
import com.aman.smart_task_manager.dto.request.RegisterRequest;
import com.aman.smart_task_manager.dto.response.AuthResponse;
import com.aman.smart_task_manager.dto.response.MessageResponse;
import com.aman.smart_task_manager.exception.BadRequestException;
import com.aman.smart_task_manager.model.Role;
import com.aman.smart_task_manager.model.User;
import com.aman.smart_task_manager.repository.UserRepository;
import com.aman.smart_task_manager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email already in use");
        }
        userRepository.save(User.builder()
                .name(request.name().trim())
                .email(request.email().trim().toLowerCase())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.USER)
                .build());
        return new MessageResponse("Registered successfully");
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        return new AuthResponse(jwtUtil.generateToken(request.email().trim().toLowerCase()));
    }
}
