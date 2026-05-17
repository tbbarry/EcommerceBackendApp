package com.backend.ecommerce.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.ecommerce.dto.UserDto;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.VerificationToken;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VerificationTokenRepository;



@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${app.url}")
    private String appUrl;

    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 🔥 REGISTER + EMAIL
    public void register(UserDto request) {
    

        // 1️⃣ Création user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEnabled(false);

        userRepository.save(user);

        // 2️⃣ Génération token
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpirationDate(LocalDateTime.now().plusHours(24));

        tokenRepository.save(verificationToken);

        // 3️⃣ Envoi email
        String link = appUrl + "/auth/verify?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Activation de votre compte",
                "Cliquez ici pour activer votre compte : " + link
        );
    }

    // 🔥 VERIFY ACCOUNT
    public void verifyAccount(String token) {

        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
    }
}
