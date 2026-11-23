package com.backend.ecommerce.security;

import com.backend.ecommerce.controller.AuthController;
import com.backend.ecommerce.dto.LoginRequest;
import com.backend.ecommerce.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTokenTest {
    @Test
    void tokenUsesFreshCredentialsAfterAuthenticationErasesThePrincipalPassword() {
        AuthenticationManager authentication = mock(AuthenticationManager.class);
        JwtUtil jwt = mock(JwtUtil.class);
        AuthService auth = mock(AuthService.class);
        CustomUserDetailsService users = mock(CustomUserDetailsService.class);
        var erasedPrincipal = User.withUsername("admin@example.test").password("hash").roles("ADMIN").build();
        var result = new UsernamePasswordAuthenticationToken(erasedPrincipal, null, erasedPrincipal.getAuthorities());
        result.eraseCredentials();
        var freshPrincipal = User.withUsername("admin@example.test").password("hash").roles("ADMIN").build();
        when(authentication.authenticate(any())).thenReturn(result);
        when(users.loadUserByUsername("admin@example.test")).thenReturn(freshPrincipal);
        when(jwt.generateToken(freshPrincipal)).thenReturn("signed-token");
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@example.test");
        request.setPassword("test-password");

        assertEquals("signed-token", new AuthController(authentication, jwt, auth, users).login(request));
        verify(jwt).generateToken(freshPrincipal);
    }
}
