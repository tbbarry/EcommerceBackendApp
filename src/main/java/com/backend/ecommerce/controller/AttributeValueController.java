package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.AttributeValueDto;
import com.backend.ecommerce.dto.AttributeValueCreateRequest;
import com.backend.ecommerce.service.AttributeValueService;
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
@RequestMapping("/api/attribute-values")
@RequiredArgsConstructor
public class AttributeValueController {

    private final AttributeValueService attributeValueService;

    // PUBLIC : récupérer toutes les valeurs d'attribut
    @GetMapping
    public ResponseEntity<List<AttributeValueDto>> findAll() {
        return ResponseEntity.ok(attributeValueService.findAll());
    }

    // PUBLIC : récupérer une valeur d'attribut par ID
    @GetMapping("/{id}")
    public ResponseEntity<AttributeValueDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(attributeValueService.findById(id));
    }

    // PUBLIC : récupérer toutes les valeurs pour un attribut spécifique
    @GetMapping("/by-attribute/{attributeId}")
    public ResponseEntity<List<AttributeValueDto>> findByAttributeId(@PathVariable Integer attributeId) {
        return ResponseEntity.ok(attributeValueService.findByAttributeId(attributeId));
    }

    // ADMIN : créer une valeur d'attribut
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AttributeValueDto> create(@Valid @RequestBody AttributeValueCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attributeValueService.create(request));
    }

    // ADMIN : modifier une valeur d'attribut
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttributeValueDto> update(@PathVariable Integer id, @Valid @RequestBody AttributeValueDto dto) {
        return ResponseEntity.ok(attributeValueService.update(id, dto));
    }

    // ADMIN : supprimer une valeur d'attribut
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        attributeValueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
