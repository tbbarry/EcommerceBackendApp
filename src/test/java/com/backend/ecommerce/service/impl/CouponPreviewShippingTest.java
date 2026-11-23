package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CartViewResponse;
import com.backend.ecommerce.dto.CouponPreviewRequest;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.service.PromotionPricingService;
import com.backend.ecommerce.service.SecurityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponPreviewShippingTest {
    @Mock SecurityService security;
    @Mock UserRepository users;
    @Mock ShippingMethodRepository shipping;
    @Mock CartService carts;
    @Mock CartItemRepository items;
    @Mock PromotionPricingService pricing;
    @InjectMocks CouponPreviewServiceImpl service;

    @Test
    void rejectsPickupMethodsInCouponPreview() {
        when(users.findById(1)).thenReturn(Optional.of(new User()));
        when(carts.getCartViewByUserId(1)).thenReturn(new CartViewResponse());
        ShippingMethod method = ShippingMethod.builder().deliveryType(DeliveryType.LOCKER).build();
        when(shipping.findByIdAndActiveTrue(2)).thenReturn(Optional.of(method));

        assertThrows(BusinessException.class, () -> service.preview(1, new CouponPreviewRequest("CODE", 2)));
        method.setDeliveryType(DeliveryType.PICKUP_POINT);
        assertThrows(BusinessException.class, () -> service.preview(1, new CouponPreviewRequest("CODE", 2)));
        verify(pricing, never()).quote(any(), any(), any(), any());
    }
}
