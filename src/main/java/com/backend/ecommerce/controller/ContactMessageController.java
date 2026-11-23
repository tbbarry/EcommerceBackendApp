package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ContactMessageRequest;
import com.backend.ecommerce.service.ContactMessageService;
import com.backend.ecommerce.service.ContactRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact-messages")
public class ContactMessageController {
    private final ContactMessageService service;
    private final ContactRateLimiter limiter;

    public ContactMessageController(ContactMessageService service, ContactRateLimiter limiter) {
        this.service = service;
        this.limiter = limiter;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody ContactMessageRequest request, HttpServletRequest servletRequest) {
        if (!limiter.allow(servletRequest.getRemoteAddr())) return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
