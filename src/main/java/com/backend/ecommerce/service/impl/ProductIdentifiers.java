package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.Product;
import java.text.Normalizer;
import java.util.Locale;
import java.util.UUID;

final class ProductIdentifiers {
    private ProductIdentifiers() {}

    static String slug(Product product) {
        String base = token(product.getName() + " " + product.getBrand() + " "
                + (product.getCategory() == null ? "" : product.getCategory().getName())).toLowerCase(Locale.ROOT);
        if (base.isEmpty()) base = "produit";
        return base.substring(0, Math.min(base.length(), 180)).replaceAll("-+$", "") + "-" + UUID.randomUUID();
    }

    static String sku(Product product) {
        String base = token(product.getName());
        if (base.isEmpty()) base = "PRODUIT";
        return base.substring(0, Math.min(base.length(), 100)).replaceAll("-+$", "") + "-" + UUID.randomUUID().toString().toUpperCase(Locale.ROOT);
    }

    static String imageAlt(Product product, AttributeValue value) {
        return value == null ? product.getName() : product.getName() + " - "
                + value.getAttribute().getName() + " : " + value.getValue();
    }

    private static String token(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "-").replaceAll("^-+|-+$", "");
    }
}
