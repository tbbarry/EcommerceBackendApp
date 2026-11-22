package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ContactMessageRequest;
import com.backend.ecommerce.dto.ContactMessageResponse;
import com.backend.ecommerce.dto.ContactReplyRequest;
import com.backend.ecommerce.entity.ContactMessage;
import com.backend.ecommerce.repository.ContactMessageRepository;
import com.backend.ecommerce.service.email.EmailService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ContactMessageService {
    private final ContactMessageRepository repository;
    private final EmailService emailService;

    public ContactMessageService(ContactMessageRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    @Transactional
    public void create(ContactMessageRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.name().trim());
        message.setEmail(request.email().trim());
        message.setMessage(request.message().trim());
        repository.save(message);
    }

    @Transactional(readOnly = true)
    public Page<ContactMessageResponse> list(int page, int size) {
        if (page < 0 || size < 1 || size > 100) throw new IllegalArgumentException("Invalid pagination.");
        return repository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt", "id")))
                .map(ContactMessageResponse::from);
    }

    @Transactional
    public ContactMessageResponse reply(int id, ContactReplyRequest request) {
        ContactMessage message = repository.findForReply(id).orElseThrow(ContactNotFoundException::new);
        if (message.getRepliedAt() != null) throw new ContactAlreadyRepliedException();
        emailService.sendContactReply(message.getEmail(), request.subject().trim(), request.message().trim());
        message.setReplySubject(request.subject().trim());
        message.setReplyMessage(request.message().trim());
        message.setRepliedAt(LocalDateTime.now());
        return ContactMessageResponse.from(message);
    }

    public static class ContactNotFoundException extends RuntimeException {}
    public static class ContactAlreadyRepliedException extends RuntimeException {}
}
