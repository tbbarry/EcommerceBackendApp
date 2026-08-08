package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.OrderCheckoutResponse;
import com.backend.ecommerce.dto.OrderDto;

import java.math.BigDecimal;

public interface OrderService extends CrudService<OrderDto, Integer> {
    BigDecimal getTotalPaidOrdersAmount();

    OrderCheckoutResponse checkout(Integer userId, CheckoutRequest request);
}
