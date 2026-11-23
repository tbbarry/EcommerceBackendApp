package com.backend.ecommerce.service.email;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactReplyEmailTest {
    @Test
    void sendsPlainTextThroughConfiguredMailerAndSurfacesFailure() throws Exception {
        JavaMailSender sender = mock(JavaMailSender.class);
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(sender.createMimeMessage()).thenReturn(message);
        EmailService service = new EmailService(sender);
        service.sendContactReply("alex@example.com", "Your question", "<b>Plain</b>");
        message.saveChanges();
        assertEquals("alex@example.com", message.getAllRecipients()[0].toString());
        assertEquals("Your question", message.getSubject());
        assertTrue(message.isMimeType("text/plain"));
        assertEquals("<b>Plain</b>", message.getContent());
        verify(sender).send(message);
        doThrow(new MailSendException("offline")).when(sender).send(message);
        assertThrows(EmailDeliveryException.class,
                () -> service.sendContactReply("alex@example.com", "Your question", "Reply"));
    }
}
