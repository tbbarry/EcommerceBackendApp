package com.backend.ecommerce.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JwtFilterPasswordChangeTest {
    private final JwtUtil jwt = mock(JwtUtil.class);
    private final CustomUserDetailsService users = mock(CustomUserDetailsService.class);
    private final JwtFilter filter = new JwtFilter(jwt, users, new ObjectMapper().findAndRegisterModules());

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void temporaryPasswordBlocksAdminButAllowsPasswordChange() throws Exception {
        when(jwt.extractUsername("token")).thenReturn("admin@example.test");
        var principal = User.withUsername("admin@example.test").password("hash").roles("ADMIN").build();
        when(users.loadUserByUsername("admin@example.test")).thenReturn(principal);
        when(jwt.validateToken("token", principal)).thenReturn(true);
        when(users.mustChangePassword("admin@example.test")).thenReturn(true);

        MockHttpServletRequest request = request("GET", "/api/admin/products");
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, new MockFilterChain());
        assertEquals(403, response.getStatus());

        request = request("PUT", "/api/users/change-password");
        response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);
        assertEquals(200, response.getStatus());
        assertEquals(request, chain.getRequest());
    }

    private MockHttpServletRequest request(String method, String path) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, path);
        request.setServletPath(path);
        request.addHeader("Authorization", "Bearer token");
        return request;
    }
}
