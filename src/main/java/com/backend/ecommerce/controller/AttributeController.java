package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.AttributeDto;
import com.backend.ecommerce.dto.AttributeCreateRequest;
import com.backend.ecommerce.dto.AttributeWithValuesDto;
import com.backend.ecommerce.service.AttributeService;
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
@RequestMapping("/api/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    // PUBLIC : récupérer tous les attributs avec leurs valeurs (pour le front)
    @GetMapping("/with-values")
    public ResponseEntity<List<AttributeWithValuesDto>> getAllWithValues() {
        return ResponseEntity.ok(attributeService.getAllAttributesWithValues());
    }

    // PUBLIC : récupérer tous les attributs avec leurs valeurs
    @GetMapping
    public ResponseEntity<List<AttributeDto>> findAll() {
        return ResponseEntity.ok(attributeService.findAllWithValues());
    }

    // PUBLIC : récupérer un attribut par ID
    @GetMapping("/{id}")
    public ResponseEntity<AttributeDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(attributeService.findById(id));
    }

    // ADMIN : créer un attribut
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<AttributeDto> create(@Valid @RequestBody AttributeCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attributeService.create(request));
    }

    // ADMIN : modifier un attribut
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AttributeDto> update(@PathVariable Integer id, @Valid @RequestBody AttributeDto dto) {
        return ResponseEntity.ok(attributeService.update(id, dto));
    }

    // ADMIN : supprimer un attribut
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        attributeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
