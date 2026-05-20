package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.MessageDto;
import com.backend.ecommerce.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(messageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(messageService.findById(id));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> findByConversationId(@PathVariable Integer conversationId) {
        return ResponseEntity.ok(messageService.findByConversationId(conversationId));
    }

    @PostMapping
    public ResponseEntity<MessageDto> create(@Valid @RequestBody MessageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody MessageDto dto
    ) {
        return ResponseEntity.ok(messageService.update(id, dto));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<MessageDto> markAsRead(@PathVariable Integer id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}