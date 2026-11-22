package com.backend.ecommerce.dto;

import com.backend.ecommerce.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record AdminOrderStatusRequest(@NotNull OrderStatus expectedStatus, @NotNull OrderStatus status) {
}
