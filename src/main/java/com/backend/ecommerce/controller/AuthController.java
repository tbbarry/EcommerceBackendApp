package com.backend.ecommerce.controller;

import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import com.backend.ecommerce.dto.LoginRequest;
import com.backend.ecommerce.dto.UserDto;
import com.backend.ecommerce.security.JwtUtil;
import com.backend.ecommerce.service.AuthService;

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
    public String register(@RequestBody UserDto userDto) {
        authService.register(userDto);
        return "Utilisateur créé ✅ Vérifiez votre email";
    }

    // 📩 VERIFY EMAIL
    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        authService.verifyAccount(token);
        return "Compte activé ✅";
    }
}