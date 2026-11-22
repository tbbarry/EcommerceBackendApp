package com.backend.ecommerce.service.email;

import jakarta.mail.BodyPart;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EmailServiceTest {
    @Test
    void accountMailEscapesEditableBodyAndKeepsTheSecureLinkAndExpiryAutomatic() throws Exception {
        JavaMailSender sender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(mimeMessage);
        EmailService service = new EmailService(sender);
        String link = "https://shop.example/auth/verify?token=real-secret";

        service.sendAccountMail("alex@example.test", "Welcome to Store",
                "Hello <img src=x onerror=alert(1)>", "Alex", link, "Store",
                EmailTemplate.ACTIVATION_ACCOUNT);

        String html = findPart(mimeMessage, "text/html");
        String text = findPart(mimeMessage, "text/plain");
        assertTrue(html.contains("Hello &lt;img src=x onerror=alert(1)&gt;"));
        assertTrue(html.contains("href=\"https://shop.example/auth/verify?token=real-secret\""));
        assertTrue(html.contains("This link expires in 24 hours."));
        assertTrue(text.contains(link));
        assertTrue(text.contains("This link expires in 24 hours."));
    }

    @Test
    void resetMailKeepsItsAutomaticLinkAndThirtyMinuteNotice() throws Exception {
        JavaMailSender sender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(mimeMessage);

        new EmailService(sender).sendAccountMail("alex@example.test", "Reset password",
                "Please choose a new password.", "Alex", "https://shop.example/auth/reset-password?token=secret",
                "Store", EmailTemplate.RESET_PASSWORD);

        String html = findPart(mimeMessage, "text/html");
        assertTrue(html.contains("href=\"https://shop.example/auth/reset-password?token=secret\""));
        assertTrue(html.contains("This link expires in 30 minutes."));
        assertTrue(html.contains("If you did not request this, you can safely ignore this email."));
    }

    @Test
    void accountSubjectCannotContainHeaderLineBreaks() {
        assertThrows(IllegalArgumentException.class, () -> new EmailService(mock(JavaMailSender.class))
                .sendAccountMail("alex@example.test", "Reset\r\nBcc: other@example.test",
                        "Message", "Alex", "https://shop.example/reset?token=secret", "Store",
                        EmailTemplate.RESET_PASSWORD));
    }

    private String findPart(MimeMessage message, String contentType) throws Exception {
        message.saveChanges();
        return findPart(message.getContent(), contentType);
    }

    private String findPart(Object content, String contentType) throws Exception {
        if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart part = multipart.getBodyPart(i);
                if (part.isMimeType(contentType)) return part.getContent().toString();
                if (part.getContent() instanceof Multipart nested) {
                    String found = findPart(nested, contentType);
                    if (found != null) return found;
                }
            }
        }
        return null;
    }
}
