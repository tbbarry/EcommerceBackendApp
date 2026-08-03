package com.backend.ecommerce.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.backend.ecommerce.dto.RegisterDto;
import com.backend.ecommerce.entity.PasswordResetToken;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.VerificationToken;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.PasswordResetTokenRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VerificationTokenRepository;
import com.backend.ecommerce.service.email.EmailService;
import com.backend.ecommerce.service.email.EmailTemplate;



@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    @Value("${app.frontend_app}")
    private String appUrl;
    @Value("${app.name}")
    private String appName;


    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordResetTokenRepository resetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 🔥 REGISTER + EMAIL
    public void register(RegisterDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé");
        }
    

        // 1️⃣ Création user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setEnabled(false);
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());

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
        String fullName = user.getFirstname() + " " + user.getLastname();

        emailService.sendMail(user.getEmail(), fullName, link, appName, EmailTemplate.ACTIVATION_ACCOUNT);
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

    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));

        resetTokenRepository.save(resetToken);

        String link = appUrl + "/auth/reset-password?token=" + token;
        

        emailService.sendMail(user.getEmail(), user.getFirstname(), link, appName, EmailTemplate.RESET_PASSWORD);
    }
    
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        // 🔥 Vérifier expiration
        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        User user = resetToken.getUser();

        // 🔐 encoder nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        // 🧹 supprimer token (important sécurité)
        resetTokenRepository.delete(resetToken);
    }
}
