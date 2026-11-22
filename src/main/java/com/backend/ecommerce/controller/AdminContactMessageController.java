package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ContactMessageResponse;
import com.backend.ecommerce.dto.ContactReplyRequest;
import com.backend.ecommerce.service.ContactMessageService;
import com.backend.ecommerce.service.email.EmailDeliveryException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/contact-messages")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactMessageController {
    private final ContactMessageService service;
    public AdminContactMessageController(ContactMessageService service) { this.service = service; }

    @GetMapping
    public Page<ContactMessageResponse> list(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        return service.list(page, size);
    }

    @PostMapping("/{id}/reply")
    public ContactMessageResponse reply(@PathVariable int id, @Valid @RequestBody ContactReplyRequest request) {
        return service.reply(id, request);
    }

    @ExceptionHandler(ContactMessageService.ContactNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Contact message not found."));
    }

    @ExceptionHandler(ContactMessageService.ContactAlreadyRepliedException.class)
    public ResponseEntity<Map<String, String>> alreadyReplied() {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "This message has already been replied to."));
    }

    @ExceptionHandler(EmailDeliveryException.class)
    public ResponseEntity<Map<String, String>> deliveryFailed() {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", "Email delivery failed. Please try again."));
    }
}
