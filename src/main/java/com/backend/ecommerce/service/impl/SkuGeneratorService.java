package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.repository.VariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.text.Normalizer;

@Service
@RequiredArgsConstructor
public class SkuGeneratorService {

    private final VariantRepository variantRepository;

    public String generateSimpleSku(String productName) {
        String base = normalizeToken(productName);
        int sequence = 1;
        String sku;
        do {
            sku = base + "-" + String.format("%03d", sequence);
            sequence++;
        } while (variantRepository.existsBySku(sku));
        return sku;
    }

    public String generateVariantSku(String productName, String color, String size) {
        String base = normalizeToken(productName) + "-" + normalizeToken(color) + "-" + normalizeToken(size);
        String sku = base;
        int suffix = 2;
        while (variantRepository.existsBySku(sku)) {
            sku = base + "-" + suffix;
            suffix++;
        }
        return sku;
    }

    private String normalizeToken(String value) {
        if (value == null || value.isBlank()) {
            return "NA";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
