package com.backend.ecommerce.security;

import com.backend.ecommerce.dto.error.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    public JwtFilter(JwtUtil jwtUtil,
                     CustomUserDetailsService userDetailsService,
                     ObjectMapper objectMapper) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.equals("/api/stripe/webhook") || path.startsWith("/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String authHeader = request.getHeader("Authorization");

            String token = null;
            String email = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                email = jwtUtil.extractUsername(token);
            }

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (userDetails.isEnabled() && jwtUtil.validateToken(token, userDetails)) {
                    if (userDetailsService.mustChangePassword(email)
                            && !(path.equals("/api/users/me") && request.getMethod().equals("GET"))
                            && !(path.equals("/api/users/change-password") && request.getMethod().equals("PUT"))) {
                        sendError(response, 403, "Password change required");
                        return;
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (ExpiredJwtException e) {
            sendError(response, 401, "Token expired");
            return;

        } catch (MalformedJwtException e) {
            sendError(response, 401, "Invalid token");
            return;

        } catch (SignatureException e) {
            sendError(response, 401, "Invalid signature");
            return;

        } catch (UnsupportedJwtException e) {
            sendError(response, 401, "Unsupported token");
            return;

        } catch (JwtException e) {
            sendError(response, 401, "Invalid token");
            return;

        } catch (IllegalArgumentException e) {
            sendError(response, 401, "Token is empty or null");
            return;
        } catch (UsernameNotFoundException e) {
            sendError(response, 401, "User not found");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void sendError(HttpServletResponse response, int status, String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(LocalDateTime.now());
        error.setStatus(status);
        error.setError(status == 403 ? "Forbidden" : "Unauthorized");
        error.setMessage(message);
        error.setErrors(null);

        objectMapper.writeValue(response.getOutputStream(), error);
    }
}
