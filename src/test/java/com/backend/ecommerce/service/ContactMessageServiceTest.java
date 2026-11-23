package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ContactMessageRequest;
import com.backend.ecommerce.dto.ContactReplyRequest;
import com.backend.ecommerce.entity.ContactMessage;
import com.backend.ecommerce.repository.ContactMessageRepository;
import com.backend.ecommerce.service.email.EmailDeliveryException;
import com.backend.ecommerce.service.email.EmailService;
import org.junit.jupiter.api.Test;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContactMessageServiceTest {
    ContactMessageRepository repository = mock(ContactMessageRepository.class);
    EmailService email = mock(EmailService.class);
    ContactMessageService service = new ContactMessageService(repository, email);

    @Test
    void persistsSubmission() {
        service.create(new ContactMessageRequest(" Alex ", "alex@example.com", " Hello "));
        verify(repository).save(argThat(message -> message.getName().equals("Alex")
                && message.getEmail().equals("alex@example.com") && message.getMessage().equals("Hello")));
    }

    @Test
    void recordsReplyOnlyAfterDeliveryAndRejectsDuplicates() {
        ContactMessage message = new ContactMessage();
        message.setEmail("alex@example.com");
        when(repository.findForReply(1)).thenReturn(Optional.of(message));
        ContactReplyRequest request = new ContactReplyRequest("Subject", "Reply");
        doThrow(new EmailDeliveryException("failure", null)).doNothing()
                .when(email).sendContactReply(any(), any(), any());
        assertThrows(EmailDeliveryException.class, () -> service.reply(1, request));
        assertNull(message.getRepliedAt());
        assertNull(message.getReplyMessage());
        var result = service.reply(1, request);
        assertNotNull(result.repliedAt());
        assertEquals("Reply", result.replyMessage());
        assertThrows(ContactMessageService.ContactAlreadyRepliedException.class, () -> service.reply(1, request));
        verify(email, times(2)).sendContactReply("alex@example.com", "Subject", "Reply");
    }

    @Test
    void rejectsMissingMessage() {
        assertThrows(ContactMessageService.ContactNotFoundException.class,
                () -> service.reply(42, new ContactReplyRequest("Subject", "Reply")));
        verifyNoInteractions(email);
    }

    @Test
    void rateLimitIsPerClientAndExpires() {
        Instant start = Instant.parse("2026-01-01T00:00:00Z");
        MutableClock clock = new MutableClock(start);
        ContactRateLimiter limiter = new ContactRateLimiter(clock);
        for (int i = 0; i < 5; i++) assertTrue(limiter.allow("one"));
        assertFalse(limiter.allow("one"));
        assertTrue(limiter.allow("two"));
        clock.now = start.plusSeconds(600);
        assertTrue(limiter.allow("one"));
    }

    static class MutableClock extends Clock {
        Instant now;
        MutableClock(Instant now) { this.now = now; }
        public ZoneOffset getZone() { return ZoneOffset.UTC; }
        public Clock withZone(java.time.ZoneId zone) { return this; }
        public Instant instant() { return now; }
    }
}
