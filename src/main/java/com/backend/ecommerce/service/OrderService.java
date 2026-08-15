package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.CheckoutResponse;
import com.backend.ecommerce.dto.OrderDto;

import java.math.BigDecimal;
import java.util.List;


public interface OrderService extends CrudService<OrderDto, Integer> {
    BigDecimal getTotalPaidOrdersAmount();

    CheckoutResponse checkout(Integer userId, CheckoutRequest request, String idempotencyKey);
    List<OrderDto> findAllByUserId(Integer userId);
}
