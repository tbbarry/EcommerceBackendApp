package com.backend.ecommerce.controller;

import com.backend.ecommerce.service.StoreSettingsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class StoreSettingsController {
    private final StoreSettingsService settings;

    public record NameRequest(@NotBlank @Size(max = 100) String name) {}
    public record ContentRequest(@Size(max = 4000) String aboutText,
                                 @Size(max = 4000) String contactText,
                                 @Email @Size(max = 254) String contactEmail) {}

    @GetMapping("/api/store-settings")
    public StoreSettingsService.PublicSettings get() {
        return settings.get();
    }

    @GetMapping("/api/store-settings/logo")
    public ResponseEntity<byte[]> logo() {
        byte[] logo = settings.logo();
        if (logo == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).contentType(MediaType.IMAGE_PNG).body(logo);
    }

    @PutMapping("/api/admin/store-settings")
    public StoreSettingsService.PublicSettings update(@Valid @RequestBody NameRequest request) {
        return settings.updateName(request.name());
    }

    @PutMapping("/api/admin/store-settings/content")
    public StoreSettingsService.PublicSettings updateContent(@Valid @RequestBody ContentRequest request) {
        return settings.updateContent(request.aboutText(), request.contactText(), request.contactEmail());
    }

    @PutMapping(value = "/api/admin/store-settings/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoreSettingsService.PublicSettings upload(@RequestParam("file") MultipartFile file) throws IOException {
        return settings.updateLogo(file);
    }

    @DeleteMapping("/api/admin/store-settings/logo")
    public StoreSettingsService.PublicSettings remove() {
        return settings.removeLogo();
    }
}
