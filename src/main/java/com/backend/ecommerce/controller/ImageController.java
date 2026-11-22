package com.backend.ecommerce.controller;

import com.backend.ecommerce.dto.ImageDto;
import com.backend.ecommerce.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

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

    /**
     * Récupère une image de produit uploadée
     */
    @GetMapping("/products/{imagePath:.+}")
    public ResponseEntity<?> getProductImage(@PathVariable String imagePath) {
        return readProductImage(imagePath);
    }

    @GetMapping("/products/{productDirectory:product_[0-9]+}/{filename:.+}")
    public ResponseEntity<?> getProductImageInDirectory(@PathVariable String productDirectory,
                                                         @PathVariable String filename) {
        return readProductImage(productDirectory + "/" + filename);
    }

    private ResponseEntity<?> readProductImage(String imagePath) {
        try {
            Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path filePath = root.resolve(imagePath).normalize();

            if (!filePath.startsWith(root) || !Files.isRegularFile(filePath)
                    || !filePath.toRealPath().startsWith(root.toRealPath())) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            String contentType = getContentType(imagePath);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(imageBytes.length)
                    .body(imageBytes);
                    
        } catch (IOException e) {
            log.error("Error reading image file: {}", imagePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getContentType(String filename) {
        if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else if (filename.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/octet-stream";
    }
}
