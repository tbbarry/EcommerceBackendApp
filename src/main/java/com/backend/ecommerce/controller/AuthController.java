package com.backend.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import com.backend.ecommerce.dto.ForgotPasswordRequest;
import com.backend.ecommerce.dto.LoginRequest;
import com.backend.ecommerce.dto.ResetPasswordRequest;
import com.backend.ecommerce.dto.RegisterDto;
import com.backend.ecommerce.security.JwtUtil;
import com.backend.ecommerce.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.authService = authService;
    }

    // 🔐 LOGIN
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        return jwtUtil.generateToken(request.getEmail());
    }

    // 🆕 REGISTER
    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterDto userDto) {
        authService.register(userDto);
        return "Utilisateur créé  Vérifiez votre email";
    }

    // VERIFY EMAIL
    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        authService.verifyAccount(token);
        return "Compte activé ";
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request.getEmail());

        return ResponseEntity.ok("Email envoyé !");
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request.getToken(), request.getNewPassword());

        return ResponseEntity.ok("Mot de passe mis à jour !");
    }
}