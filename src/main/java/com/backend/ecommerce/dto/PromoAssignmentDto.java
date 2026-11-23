package com.backend.ecommerce.dto;

public record PromoAssignmentDto(Integer id, String type, Integer productId, String productName,
                                 Integer variantId, String variantSku) {}
