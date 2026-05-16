package com.backend.ecommerce.controller;

import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import com.backend.ecommerce.dto.LoginRequest;
import com.backend.ecommerce.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        // 🔐 Authentification (email + password)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // 🎟 Génération du JWT
        return jwtUtil.generateToken(request.getEmail());
    }
}
