package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ConversationDto;
import com.backend.ecommerce.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(conversationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(conversationService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> findByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(conversationService.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<ConversationDto> create(@Valid @RequestBody ConversationDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(conversationService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConversationDto> update(
            @PathVariable Integer id,
            @Valid @RequestBody ConversationDto dto
    ) {
        return ResponseEntity.ok(conversationService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        conversationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}