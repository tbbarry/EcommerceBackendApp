package com.backend.ecommerce.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    @Test
    void changingPasswordInvalidatesPreviouslyIssuedTokens() {
        JwtUtil jwt = new JwtUtil();
        ReflectionTestUtils.setField(jwt, "SECRET", "test-signing-key-with-at-least-thirty-two-bytes");
        ReflectionTestUtils.setField(jwt, "EXPIRATION", 60000L);
        var oldUser = User.withUsername("admin@example.test").password("previous-hash").roles("ADMIN").build();
        var updatedUser = User.withUsername("admin@example.test").password("new-hash").roles("ADMIN").build();

        String token = jwt.generateToken(oldUser);
        assertTrue(jwt.validateToken(token, oldUser));
        assertFalse(jwt.validateToken(token, updatedUser));
    }
}
