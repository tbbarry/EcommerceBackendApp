package com.backend.ecommerce.service;

import com.backend.ecommerce.entity.ShippingMethod;

import java.math.BigDecimal;

public interface ShippingCalculationService {

    BigDecimal calculateShippingCost(BigDecimal cartSubtotal, ShippingMethod shippingMethod);
}
