package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ImageDto;
import com.backend.ecommerce.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    // Public : voir toutes les images
    @GetMapping
    public ResponseEntity<List<ImageDto>> findAll() {
        return ResponseEntity.ok(imageService.findAll());
    }

    // Public : voir une image
    @GetMapping("/{id}")
    public ResponseEntity<ImageDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(imageService.findById(id));
    }

    // ADMIN uniquement : créer une image
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ImageDto> create(@Valid @RequestBody ImageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(imageService.create(dto));
    }

    // ADMIN uniquement : modifier une image
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ImageDto> update(@PathVariable Integer id, @Valid @RequestBody ImageDto dto) {
        return ResponseEntity.ok(imageService.update(id, dto));
    }

    // ADMIN uniquement : supprimer une image
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        imageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

