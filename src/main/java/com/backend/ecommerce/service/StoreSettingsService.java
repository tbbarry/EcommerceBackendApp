package com.backend.ecommerce.service;

import com.backend.ecommerce.entity.StoreSettings;
import com.backend.ecommerce.repository.StoreSettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.IIOException;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.UUID;

@Service
public class StoreSettingsService {
    private static final int MAX_BYTES = 1024 * 1024;
    private static final int MAX_DIMENSION = 2048;
    private final StoreSettingsRepository repository;
    private final String defaultName;

    public StoreSettingsService(StoreSettingsRepository repository, @Value("${app.name}") String defaultName) {
        this.repository = repository;
        this.defaultName = defaultName;
    }

    public record PublicSettings(String name, String logoVersion, String aboutText, String contactText,
                                 String contactEmail) {}

    @Transactional(readOnly = true)
    public PublicSettings get() {
        return repository.findById(1)
                .map(this::publicSettings)
                .orElseGet(() -> new PublicSettings(defaultName, null, "", "", ""));
    }

    public String getName() {
        return get().name();
    }

    @Transactional(readOnly = true)
    public byte[] logo() {
        return repository.findById(1).map(StoreSettings::getLogo).orElse(null);
    }

    @Transactional
    public PublicSettings updateName(String name) {
        String normalized = name.trim();
        if (normalized.isEmpty() || normalized.length() > 100 || normalized.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Enter a store name of up to 100 characters.");
        }
        StoreSettings settings = settings();
        settings.setName(normalized);
        repository.save(settings);
        return publicSettings(settings);
    }

    @Transactional
    public PublicSettings updateContent(String aboutText, String contactText, String contactEmail) {
        String email = normalize(contactEmail);
        if (email.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid contact email.");
        }
        StoreSettings settings = settings();
        settings.setAboutText(normalize(aboutText));
        settings.setContactText(normalize(contactText));
        settings.setContactEmail(email);
        repository.save(settings);
        return publicSettings(settings);
    }

    @Transactional
    public PublicSettings updateLogo(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Logo must be a PNG or JPEG under 1 MB.");
        }
        byte[] bytes = file.getBytes();
        try (ImageInputStream input = ImageIO.createImageInputStream(new ByteArrayInputStream(bytes))) {
            if (input == null) throw new IllegalArgumentException("Invalid image.");
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw new IllegalArgumentException("Logo must be a PNG or JPEG.");
            ImageReader reader = readers.next();
            try {
                String format = reader.getFormatName();
                if (!format.equalsIgnoreCase("png") && !format.equalsIgnoreCase("jpeg")) {
                    throw new IllegalArgumentException("Logo must be a PNG or JPEG.");
                }
                reader.setInput(input, true, true);
                if (reader.getWidth(0) > MAX_DIMENSION || reader.getHeight(0) > MAX_DIMENSION) {
                    throw new IllegalArgumentException("Logo dimensions must not exceed 2048 pixels.");
                }
                BufferedImage image = reader.read(0);
                if (image == null) throw new IllegalArgumentException("Invalid image.");
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                if (!ImageIO.write(image, "png", output) || output.size() > MAX_BYTES * 4) {
                    throw new IllegalArgumentException("Logo could not be processed.");
                }
                StoreSettings settings = settings();
                settings.setLogo(output.toByteArray());
                settings.setLogoVersion(UUID.randomUUID().toString());
                repository.save(settings);
                return publicSettings(settings);
            } finally {
                reader.dispose();
            }
        } catch (IIOException ex) {
            throw new IllegalArgumentException("Invalid image.", ex);
        }
    }

    @Transactional
    public PublicSettings removeLogo() {
        StoreSettings settings = settings();
        settings.setLogo(null);
        settings.setLogoVersion(null);
        repository.save(settings);
        return publicSettings(settings);
    }

    private PublicSettings publicSettings(StoreSettings settings) {
        return new PublicSettings(settings.getName(), settings.getLogoVersion(), normalize(settings.getAboutText()),
                normalize(settings.getContactText()), normalize(settings.getContactEmail()));
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private StoreSettings settings() {
        return repository.findById(1).orElseGet(() -> {
            StoreSettings settings = new StoreSettings();
            settings.setName(defaultName);
            return settings;
        });
    }
}
