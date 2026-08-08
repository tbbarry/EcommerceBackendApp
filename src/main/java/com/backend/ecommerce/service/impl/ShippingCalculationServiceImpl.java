package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.service.ShippingCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ShippingCalculationServiceImpl implements ShippingCalculationService {

    @Override
    public BigDecimal calculateShippingCost(BigDecimal cartSubtotal, ShippingMethod shippingMethod) {
        if (cartSubtotal == null || shippingMethod == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal threshold = shippingMethod.getFreeShippingThreshold();
        if (threshold != null && cartSubtotal.compareTo(threshold) >= 0) {
            return BigDecimal.ZERO;
        }

        return shippingMethod.getPrice() != null ? shippingMethod.getPrice() : BigDecimal.ZERO;
    }
}
