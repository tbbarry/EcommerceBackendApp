package com.backend.ecommerce.service.email;

import com.backend.ecommerce.entity.OrderItem;

public record OrderEmailItem(String name, String color, String size, int quantity) {
    public static OrderEmailItem from(OrderItem item) {
        return new OrderEmailItem(item.getProductNameSnapshot(), item.getColorSnapshot(),
                item.getSizeSnapshot(), item.getQuantity());
    }

    public String options() {
        String colorText = color == null || color.isBlank() ? "" : color.trim();
        String sizeText = size == null || size.isBlank() ? "" : size.trim();
        if (colorText.isEmpty()) return sizeText;
        if (sizeText.isEmpty()) return colorText;
        return colorText + " · " + sizeText;
    }
}
