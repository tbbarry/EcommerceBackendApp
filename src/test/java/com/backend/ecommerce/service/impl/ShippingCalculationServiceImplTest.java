package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.ShippingMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class ShippingCalculationServiceImplTest {

    private final ShippingCalculationServiceImpl shippingCalculationService = new ShippingCalculationServiceImpl();

    @Test
    void shouldReturnZeroWhenFreeShippingThresholdReached() {
        ShippingMethod method = ShippingMethod.builder()
                .price(new BigDecimal("5.99"))
                .deliveryType(DeliveryType.HOME)
                .freeShippingThreshold(new BigDecimal("100.00"))
                .minDeliveryDays(3)
                .maxDeliveryDays(5)
                .name("Standard")
                .code("STD")
                .active(true)
                .build();

        BigDecimal result = shippingCalculationService.calculateShippingCost(new BigDecimal("100.00"), method);

        assertThat(result).isEqualByComparingTo("0.00");
    }

    @Test
    void shouldReturnMethodPriceWhenThresholdNotReached() {
        ShippingMethod method = ShippingMethod.builder()
                .price(new BigDecimal("12.99"))
                .deliveryType(DeliveryType.HAND_TO_HAND)
                .freeShippingThreshold(new BigDecimal("150.00"))
                .minDeliveryDays(1)
                .maxDeliveryDays(2)
                .name("Express")
                .code("EXP")
                .active(true)
                .build();

        BigDecimal result = shippingCalculationService.calculateShippingCost(new BigDecimal("99.00"), method);

        assertThat(result).isEqualByComparingTo("12.99");
    }
}
