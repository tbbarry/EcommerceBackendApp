package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.RegisterDto;
import com.backend.ecommerce.entity.PasswordResetToken;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.VerificationToken;
import com.backend.ecommerce.repository.PasswordResetTokenRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VerificationTokenRepository;
import com.backend.ecommerce.service.email.EmailService;
import com.backend.ecommerce.service.email.EmailTemplate;
import com.backend.ecommerce.service.email.OrderMessageTemplateService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceAccountEmailTest {
    private final UserRepository users = mock(UserRepository.class);
    private final VerificationTokenRepository verificationTokens = mock(VerificationTokenRepository.class);
    private final PasswordResetTokenRepository resetTokens = mock(PasswordResetTokenRepository.class);
    private final PasswordEncoder passwords = mock(PasswordEncoder.class);
    private final EmailService emails = mock(EmailService.class);
    private final StoreSettingsService storeSettings = mock(StoreSettingsService.class);
    private final OrderMessageTemplateService templates = mock(OrderMessageTemplateService.class);
    private final AuthService service = new AuthService(users, verificationTokens, resetTokens, passwords, emails,
            storeSettings, templates);

    AuthServiceAccountEmailTest() {
        ReflectionTestUtils.setField(service, "appUrl", "https://shop.example");
        when(storeSettings.getName()).thenReturn("Dynamic Site");
    }

    @Test
    void registrationUsesConfiguredActivationContentAndDynamicNameWithoutChangingTokenLifetime() {
        when(users.existsByEmail("alex@example.test")).thenReturn(false);
        when(passwords.encode("Password1!")).thenReturn("encoded");
        when(templates.get(OrderMessageTemplateService.ACCOUNT_ACTIVATION)).thenReturn(
                new OrderMessageTemplateService.Template(OrderMessageTemplateService.ACCOUNT_ACTIVATION,
                        "Join {{site_name}}", "Welcome {{user_name}} to {{site_name}}."));
        RegisterDto request = new RegisterDto();
        request.setFirstname("Alex");
        request.setLastname("Example");
        request.setEmail("alex@example.test");
        request.setPassword("Password1!");
        LocalDateTime before = LocalDateTime.now();

        service.register(request);

        ArgumentCaptor<VerificationToken> token = ArgumentCaptor.forClass(VerificationToken.class);
        verify(verificationTokens).save(token.capture());
        assertTrue(token.getValue().getExpirationDate().isAfter(before.plusHours(24).minusSeconds(1)));
        assertTrue(token.getValue().getExpirationDate().isBefore(LocalDateTime.now().plusHours(24).plusSeconds(1)));
        verify(emails).sendAccountMail(eq("alex@example.test"), eq("Join Dynamic Site"),
                eq("Welcome Alex Example to Dynamic Site."), eq("Alex Example"),
                eq("https://shop.example/auth/verify?token=" + token.getValue().getToken()),
                eq("Dynamic Site"), eq(EmailTemplate.ACTIVATION_ACCOUNT));
    }

    @Test
    void passwordResetUsesConfiguredContentAndDynamicNameWhileKeepingItsThirtyMinuteLink() {
        User user = new User();
        user.setFirstname("Alex");
        user.setLastname("Example");
        user.setEmail("alex@example.test");
        when(users.findByEmail("alex@example.test")).thenReturn(Optional.of(user));
        when(templates.get(OrderMessageTemplateService.PASSWORD_RESET)).thenReturn(
                new OrderMessageTemplateService.Template(OrderMessageTemplateService.PASSWORD_RESET,
                        "Reset at {{site_name}}", "Hello {{user_name}}. Contact {{site_name}} if needed."));
        LocalDateTime before = LocalDateTime.now();

        service.forgotPassword("alex@example.test");

        ArgumentCaptor<PasswordResetToken> token = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(resetTokens).save(token.capture());
        assertTrue(token.getValue().getExpirationDate().isAfter(before.plusMinutes(30).minusSeconds(1)));
        assertTrue(token.getValue().getExpirationDate().isBefore(LocalDateTime.now().plusMinutes(30).plusSeconds(1)));
        verify(emails).sendAccountMail(eq("alex@example.test"), eq("Reset at Dynamic Site"),
                eq("Hello Alex. Contact Dynamic Site if needed."), eq("Alex"),
                eq("https://shop.example/auth/reset-password?token=" + token.getValue().getToken()),
                eq("Dynamic Site"), eq(EmailTemplate.RESET_PASSWORD));
    }

    @Test
    void resetHandlesMissingNamesAndNewlinesInSubjectVariables() {
        User user = new User();
        user.setEmail("alex@example.test");
        when(users.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(templates.get(OrderMessageTemplateService.PASSWORD_RESET)).thenReturn(
                new OrderMessageTemplateService.Template(OrderMessageTemplateService.PASSWORD_RESET,
                        "Reset for {{user_name}}", "Hello {{user_name}}."));
        service.forgotPassword(user.getEmail());
        verify(emails).sendAccountMail(eq(user.getEmail()), eq("Reset for there"), eq("Hello there."),
                eq("there"), anyString(), eq("Dynamic Site"), eq(EmailTemplate.RESET_PASSWORD));

        user.setFirstname("Alex\nExample");
        service.forgotPassword(user.getEmail());
        verify(emails).sendAccountMail(eq(user.getEmail()), eq("Reset for Alex Example"),
                eq("Hello Alex\nExample."), eq("Alex\nExample"), anyString(),
                eq("Dynamic Site"), eq(EmailTemplate.RESET_PASSWORD));
    }
}
