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
import com.backend.ecommerce.service.email.OrderMessageTemplateService;



@Service
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final StoreSettingsService storeSettingsService;
    private final OrderMessageTemplateService orderMessageTemplates;
    @Value("${app.frontend_app}")
    private String appUrl;


    public AuthService(UserRepository userRepository,
                       VerificationTokenRepository tokenRepository,
                       PasswordResetTokenRepository resetTokenRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService,
                       StoreSettingsService storeSettingsService,
                       OrderMessageTemplateService orderMessageTemplates) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.storeSettingsService = storeSettingsService;
        this.orderMessageTemplates = orderMessageTemplates;
    }

    // 🔥 REGISTER + EMAIL
    public void register(RegisterDto request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé", "EMAIL_ALREADY_USED");
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
        String fullName = displayName(user);
        String siteName = storeSettingsService.getName();
        OrderMessageTemplateService.Template emailTemplate =
                orderMessageTemplates.get(OrderMessageTemplateService.ACCOUNT_ACTIVATION);

        emailService.sendAccountMail(user.getEmail(),
                OrderMessageTemplateService.resolveAccount(emailTemplate.subject(), subjectName(fullName), siteName),
                OrderMessageTemplateService.resolveAccount(emailTemplate.message(), fullName, siteName),
                fullName, link, siteName, EmailTemplate.ACTIVATION_ACCOUNT);
    }

    // 🔥 VERIFY ACCOUNT
    public void verifyAccount(String token) {

        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token invalide", "INVALID_TOKEN"));

        if (verificationToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Token expiré", "TOKEN_EXPIRED");
        }

        User user = verificationToken.getUser();
        user.setEnabled(true);

        userRepository.save(user);
    }

    public void forgotPassword(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found", "USER_NOT_FOUND"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(LocalDateTime.now().plusMinutes(30));

        resetTokenRepository.save(resetToken);

        String link = appUrl + "/auth/reset-password?token=" + token;

        String siteName = storeSettingsService.getName();
        String userName = user.getFirstname() == null || user.getFirstname().isBlank()
                ? displayName(user) : user.getFirstname();
        OrderMessageTemplateService.Template emailTemplate =
                orderMessageTemplates.get(OrderMessageTemplateService.PASSWORD_RESET);
        emailService.sendAccountMail(user.getEmail(),
                OrderMessageTemplateService.resolveAccount(emailTemplate.subject(), subjectName(userName), siteName),
                OrderMessageTemplateService.resolveAccount(emailTemplate.message(), userName, siteName),
                userName, link, siteName, EmailTemplate.RESET_PASSWORD);
    }

    private String displayName(User user) {
        String first = user.getFirstname() == null ? "" : user.getFirstname();
        String last = user.getLastname() == null ? "" : user.getLastname();
        String name = (first + " " + last).trim();
        return name.isEmpty() ? "there" : name;
    }

    private String subjectName(String name) {
        return name.replace('\r', ' ').replace('\n', ' ');
    }
    
    public void resetPassword(String token, String newPassword) {

        PasswordResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token invalide", "INVALID_TOKEN"));

        // 🔥 Vérifier expiration
        if (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        User user = resetToken.getUser();

        // 🔐 encoder nouveau mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);

        userRepository.save(user);

        // 🧹 supprimer token (important sécurité)
        resetTokenRepository.delete(resetToken);
    }
}
