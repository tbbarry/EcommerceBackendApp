package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadServiceImpl implements ImageUploadService {

    @Value("${app.upload.dir:uploads/products}")
    private String uploadDir;

    @Value("${app.upload.base-url:/api/images}")
    private String baseUrl;

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "webp"
    ));

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    @Override
    public String uploadProductImage(MultipartFile file, Integer productId) throws IOException {
        validateFile(file);

        String uploadPath = uploadDir + File.separator + "product_" + productId;
        Path uploadDirectory = Paths.get(uploadPath);

        if (!Files.exists(uploadDirectory)) {
            Files.createDirectories(uploadDirectory);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID() + "." + fileExtension;
        Path filePath = uploadDirectory.resolve(uniqueFilename);

        Files.write(filePath, file.getBytes());

        String relativePath = "product_" + productId + File.separator + uniqueFilename;
        log.info("Image uploaded successfully: {}", relativePath);

        return relativePath;
    }

    @Override
    public String storeProductImageData(String data, Integer productId) {
        if (data != null && (data.startsWith("https://") || data.startsWith("http://"))) return data;
        if (data == null || !(data.startsWith("data:image/jpeg;base64,") || data.startsWith("data:image/png;base64,"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image must be a JPEG or PNG data URL");
        }
        int comma = data.indexOf(',');
        String extension = data.startsWith("data:image/png;") ? "png" : "jpg";
        if (data.length() - comma - 1 > ((MAX_FILE_SIZE + 2) / 3) * 4) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image exceeds 10MB limit");
        }
        final byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(data.substring(comma + 1));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image data", ex);
        }
        if (bytes.length == 0 || bytes.length > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid image size");
        }
        String relative = "product_" + productId + File.separator + UUID.randomUUID() + "." + extension;
        Path path = Paths.get(uploadDir, relative);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, bytes);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save product image", ex);
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) deleteProductImage(relative);
                }
            });
        }
        return relative;
    }

    @Override
    public void deleteProductImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return;
            }

            Path filePath = Paths.get(uploadDir, imagePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                log.info("Image deleted: {}", imagePath);
            }
        } catch (IOException e) {
            log.error("Error deleting image: {}", imagePath, e);
        }
    }

    @Override
    public String getImageUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return baseUrl + "/" + imagePath.replace(File.separator, "/");
    }

    private void validateFile(MultipartFile file) throws IllegalArgumentException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new IllegalArgumentException("Filename is null");
        }

        String extension = getFileExtension(filename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("File type not allowed. Allowed: " + ALLOWED_EXTENSIONS);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }
}
