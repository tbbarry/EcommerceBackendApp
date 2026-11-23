package com.backend.ecommerce.dto;

import com.backend.ecommerce.entity.ContactMessage;
import java.time.LocalDateTime;

public record ContactMessageResponse(Integer id, String name, String email, String message,
                                     LocalDateTime createdAt, LocalDateTime repliedAt,
                                     String replySubject, String replyMessage) {
    public static ContactMessageResponse from(ContactMessage m) {
        return new ContactMessageResponse(m.getId(), m.getName(), m.getEmail(), m.getMessage(),
                m.getCreatedAt(), m.getRepliedAt(), m.getReplySubject(), m.getReplyMessage());
    }
}
