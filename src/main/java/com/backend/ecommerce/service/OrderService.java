package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.OrderDto;

import java.math.BigDecimal;

public interface OrderService extends CrudService<OrderDto, Integer> {
    BigDecimal getTotalPaidOrdersAmount();
}
