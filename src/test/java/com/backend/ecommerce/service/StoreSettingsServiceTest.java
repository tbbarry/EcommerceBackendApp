package com.backend.ecommerce.service;

import com.backend.ecommerce.entity.StoreSettings;
import com.backend.ecommerce.repository.StoreSettingsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StoreSettingsServiceTest {
    private final StoreSettingsRepository repository = mock(StoreSettingsRepository.class);
    private final StoreSettingsService service = new StoreSettingsService(repository, "Default");

    @Test
    void defaultsAndSavesNameWithoutReplacingLogo() {
        assertEquals("Default", service.getName());
        StoreSettings settings = new StoreSettings();
        settings.setName("Old");
        settings.setLogo(new byte[] {1});
        when(repository.findById(1)).thenReturn(Optional.of(settings));
        assertEquals("New", service.updateName(" New ").name());
        assertArrayEquals(new byte[] {1}, settings.getLogo());
        assertThrows(IllegalArgumentException.class, () -> service.updateName(" \n "));
    }

    @Test
    void validatesImageContentsAndSupportsRemoval() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> service.updateLogo(
                new MockMultipartFile("file", "logo.png", "image/png", "not an image".getBytes())));
        ByteArrayOutputStream tooWide = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(2049, 1, BufferedImage.TYPE_INT_ARGB), "png", tooWide);
        assertThrows(IllegalArgumentException.class, () -> service.updateLogo(
                new MockMultipartFile("file", "logo.png", "image/png", tooWide.toByteArray())));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB), "png", output);
        StoreSettings settings = new StoreSettings();
        settings.setName("Store");
        when(repository.findById(1)).thenReturn(Optional.of(settings));
        var saved = service.updateLogo(new MockMultipartFile("file", "logo.png", "image/png", output.toByteArray()));
        assertNotNull(saved.logoVersion());
        assertNotNull(settings.getLogo());
        assertNull(service.removeLogo().logoVersion());
        assertNull(settings.getLogo());
    }

    @Test
    void contentUpdatesPreserveTheBrandAndLogo() {
        StoreSettings settings = new StoreSettings();
        settings.setName("Store");
        settings.setLogo(new byte[] {1});
        when(repository.findById(1)).thenReturn(Optional.of(settings));
        var saved = service.updateContent(" Our story ", " Get in touch ", " hello@example.com ");
        assertEquals("Store", saved.name());
        assertEquals("Our story", saved.aboutText());
        assertEquals("Get in touch", saved.contactText());
        assertEquals("hello@example.com", saved.contactEmail());
        assertArrayEquals(new byte[] {1}, settings.getLogo());
        assertThrows(IllegalArgumentException.class, () -> service.updateContent("", "", "bad\r\nemail"));
    }
}
