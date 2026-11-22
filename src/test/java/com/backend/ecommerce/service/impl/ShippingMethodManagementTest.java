package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ShippingMethodRequest;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShippingMethodManagementTest {
    @Mock ShippingMethodRepository repository;
    ShippingMethodServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ShippingMethodServiceImpl(repository);
    }

    private ShippingMethodRequest request() {
        ShippingMethodRequest request = new ShippingMethodRequest();
        request.setName(" Standard ");
        request.setCode("standard");
        request.setDeliveryType(DeliveryType.HOME);
        request.setMinDeliveryDays(2);
        request.setMaxDeliveryDays(4);
        request.setPrice(new BigDecimal("5.00"));
        request.setActive(true);
        return request;
    }

    @Test
    void savesNormalizedMethodAndAllowsDeactivation() {
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        var created = service.createMethod(request());
        assertEquals("Standard", created.getName());
        assertEquals("STANDARD", created.getCode());
        assertTrue(created.isActive());

        ShippingMethod existing = new ShippingMethod();
        existing.setId(5);
        when(repository.findById(5)).thenReturn(Optional.of(existing));
        when(repository.findByCodeIgnoreCase("STANDARD")).thenReturn(Optional.of(existing));
        var update = request();
        update.setActive(false);
        assertFalse(service.updateMethod(5, update).isActive());
    }

    @Test
    void rejectsDuplicateCodeAndInvalidDeliveryRange() {
        ShippingMethod existing = new ShippingMethod();
        existing.setId(5);
        when(repository.findByCodeIgnoreCase("STANDARD")).thenReturn(Optional.of(existing));
        assertThrows(BusinessException.class, () -> service.createMethod(request()));
        var invalid = request();
        invalid.setMaxDeliveryDays(1);
        assertThrows(BusinessException.class, () -> service.createMethod(invalid));
        verify(repository, never()).save(any());
    }

    @Test
    void publicListUsesOnlyActiveMethods() {
        when(repository.findByActiveTrueOrderByPriceAsc()).thenReturn(List.of(
                ShippingMethod.builder().active(true).deliveryType(DeliveryType.HOME).build(),
                ShippingMethod.builder().active(true).deliveryType(DeliveryType.HAND_TO_HAND).build(),
                ShippingMethod.builder().active(true).deliveryType(DeliveryType.PICKUP_POINT).build(),
                ShippingMethod.builder().active(true).deliveryType(DeliveryType.LOCKER).build()));
        assertEquals(2, service.findActiveMethods().size());
        verify(repository).findByActiveTrueOrderByPriceAsc();
    }
}
