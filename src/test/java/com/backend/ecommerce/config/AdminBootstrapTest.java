package com.backend.ecommerce.config;

import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminBootstrapTest {
    private final UserRepository users = mock(UserRepository.class);
    private final BCryptPasswordEncoder passwords = new BCryptPasswordEncoder();

    @Test
    void createsEnabledAdminWithHashedTemporaryPasswordAndRequiredChange() {
        when(users.findByEmail("admin@example.test")).thenReturn(Optional.empty());

        new AdminBootstrap(users, passwords, "admin@example.test", "temporary-secret-123").run(null);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(users).save(captor.capture());
        User admin = captor.getValue();
        assertEquals("admin@example.test", admin.getEmail());
        assertEquals("ADMIN", admin.getRole());
        assertTrue(admin.isEnabled());
        assertTrue(admin.isMustChangePassword());
        assertNotEquals("temporary-secret-123", admin.getPassword());
        assertTrue(passwords.matches("temporary-secret-123", admin.getPassword()));
    }

    @Test
    void existingAdminIsNotResetEvenWithoutInitialPassword() {
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setEnabled(true);
        admin.setPassword("existing-hash");
        when(users.findByEmail("admin@example.test")).thenReturn(Optional.of(admin));

        new AdminBootstrap(users, passwords, "admin@example.test", "").run(null);

        assertEquals("existing-hash", admin.getPassword());
        assertFalse(admin.isMustChangePassword());
        verify(users, never()).save(any());
    }

    @Test
    void refusesToPromoteAnExistingUser() {
        User user = new User();
        user.setRole("USER");
        when(users.findByEmail("admin@example.test")).thenReturn(Optional.of(user));

        assertThrows(IllegalStateException.class,
                () -> new AdminBootstrap(users, passwords, "admin@example.test", "temporary-secret-123").run(null));
        verify(users, never()).save(any());
    }

    @Test
    void missingOrInvalidConfigurationDoesNotCreateAnAccount() {
        new AdminBootstrap(users, passwords, "", "").run(null);
        assertThrows(IllegalStateException.class,
                () -> new AdminBootstrap(users, passwords, "", "temporary-secret-123").run(null));
        when(users.findByEmail("admin@example.test")).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
                () -> new AdminBootstrap(users, passwords, "admin@example.test", "").run(null));
        assertThrows(IllegalStateException.class,
                () -> new AdminBootstrap(users, passwords, "admin@example.test", "short").run(null));
        assertThrows(IllegalStateException.class,
                () -> new AdminBootstrap(users, passwords, "admin@example.test", "a".repeat(73)).run(null));
        verify(users, never()).save(any());
    }
}
