package com.backend.ecommerce.config;

import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository users;
    private final PasswordEncoder passwords;
    private final String email;
    private final String initialPassword;

    public AdminBootstrap(UserRepository users, PasswordEncoder passwords,
                          @Value("${ADMIN_BOOTSTRAP_EMAIL:}") String email,
                          @Value("${ADMIN_BOOTSTRAP_PASSWORD:}") String initialPassword) {
        this.users = users;
        this.passwords = passwords;
        this.email = email.trim();
        this.initialPassword = initialPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (email.isEmpty()) {
            if (!initialPassword.isBlank()) {
                throw new IllegalStateException("ADMIN_BOOTSTRAP_EMAIL is required when ADMIN_BOOTSTRAP_PASSWORD is set");
            }
            log.warn("Admin bootstrap is not configured; no initial administrator will be created");
            return;
        }

        Optional<User> existing = users.findByEmail(email);
        if (existing.isPresent()) {
            User user = existing.get();
            if (!"ADMIN".equals(user.getRole())) {
                throw new IllegalStateException("The configured admin email belongs to a non-admin account");
            }
            if (!user.isEnabled()) {
                log.warn("The configured admin account is disabled; it will not be modified");
            }
            return;
        }

        int passwordBytes = initialPassword.getBytes(StandardCharsets.UTF_8).length;
        if (initialPassword.isBlank() || initialPassword.length() < 12 || passwordBytes > 72) {
            throw new IllegalStateException(
                    "ADMIN_BOOTSTRAP_PASSWORD must contain at least 12 characters and at most 72 UTF-8 bytes");
        }

        User admin = new User();
        admin.setFirstname("Admin");
        admin.setLastname("System");
        admin.setEmail(email);
        admin.setPassword(passwords.encode(initialPassword));
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        admin.setMustChangePassword(true);
        users.save(admin);
        log.info("Initial administrator created; password change is required");
    }
}
