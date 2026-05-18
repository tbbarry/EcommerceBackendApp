package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ProductDto;
import com.backend.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // PUBLIC : voir tous les produits
    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            return ResponseEntity.ok(productService.findAllPaginated(page, size));
        }

        return ResponseEntity.ok(productService.findAll());
    }

    // PUBLIC : voir le détail d'un produit
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    // ADMIN uniquement : créer un produit
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> create(@Valid @RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(dto));
    }

    // ADMIN uniquement : modifier un produit
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Integer id, @Valid @RequestBody ProductDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    // ADMIN uniquement : supprimer un produit
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
