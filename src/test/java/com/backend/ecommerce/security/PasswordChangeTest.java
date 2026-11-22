package com.backend.ecommerce.security;

import com.backend.ecommerce.dto.ChangePasswordRequest;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordChangeTest {
    @Test
    void replacingTemporaryPasswordClearsTheRestrictionAndStoresAHash() {
        UserRepository users = mock(UserRepository.class);
        var encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setPassword(encoder.encode("old-test-password"));
        user.setMustChangePassword(true);
        when(users.findByEmail("admin@example.test")).thenReturn(Optional.of(user));
        UserServiceImpl service = new UserServiceImpl(users, encoder);

        service.changePassword("admin@example.test", new ChangePasswordRequest("old-test-password", "new-test-password"));

        assertFalse(user.isMustChangePassword());
        assertTrue(encoder.matches("new-test-password", user.getPassword()));
        verify(users).save(user);
    }

    @Test
    void incorrectCurrentPasswordCannotRemoveTheRestriction() {
        UserRepository users = mock(UserRepository.class);
        var encoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setPassword(encoder.encode("old-test-password"));
        user.setMustChangePassword(true);
        when(users.findByEmail("admin@example.test")).thenReturn(Optional.of(user));
        UserServiceImpl service = new UserServiceImpl(users, encoder);

        assertThrows(IllegalArgumentException.class, () -> service.changePassword(
                "admin@example.test", new ChangePasswordRequest("wrong", "new-test-password")));
        assertTrue(user.isMustChangePassword());
        verify(users, never()).save(any());
    }
}
