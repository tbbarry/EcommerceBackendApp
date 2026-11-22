package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.VariantAttributeValue;

record VariantDescriptor(String color, String size) {
    static VariantDescriptor from(Variant variant) {
        String color = null;
        String size = null;
        for (VariantAttributeValue link : variant.getAttributeValues()) {
            AttributeValue value = link.getAttributeValue();
            if (value == null || value.getAttribute() == null || value.getAttribute().getName() == null) {
                continue;
            }
            String name = value.getAttribute().getName();
            if (name.equalsIgnoreCase("Couleur") || name.equalsIgnoreCase("Color")) {
                color = value.getValue();
            }
            if (name.equalsIgnoreCase("Taille") || name.equalsIgnoreCase("Size")) {
                size = value.getValue();
            }
        }
        return new VariantDescriptor(color, size);
    }
}
