package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CartViewResponse;
import com.backend.ecommerce.dto.CouponPreviewRequest;
import com.backend.ecommerce.dto.CouponPreviewResponse;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.service.CouponPreviewService;
import com.backend.ecommerce.service.SecurityService;
import com.backend.ecommerce.service.PromotionPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponPreviewServiceImpl implements CouponPreviewService {

    private final SecurityService securityService;
    private final UserRepository userRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final PromotionPricingService promotionPricingService;

    @Override
    public CouponPreviewResponse preview(Integer userId, CouponPreviewRequest request) {
        Integer resolvedUserId = userId != null ? userId : securityService.getCurrentUserIdOrThrow();

        User user = userRepository.findById(resolvedUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + resolvedUserId));

        CartViewResponse cart = cartService.getCartViewByUserId(resolvedUserId);
        ShippingMethod shippingMethod = null;
        if (request.getShippingMethodId() != null) {
            shippingMethod = shippingMethodRepository.findByIdAndActiveTrue(request.getShippingMethodId())
                    .orElseThrow(() -> new ResourceNotFoundException("ShippingMethod not found with id: " + request.getShippingMethodId()));
            if (!DeliveryType.supportedAtCheckout(shippingMethod.getDeliveryType())) {
                throw new BusinessException("Ce mode de livraison n'est pas disponible.", "SHIPPING_METHOD_UNAVAILABLE");
            }
        }

        PromotionPricingService.Quote quote = promotionPricingService.quote(
                cartItemRepository.findByCartId(cart.getCartId()), user, request.getCouponCode(), shippingMethod);

        return CouponPreviewResponse.builder()
                .couponCode(quote.coupon() == null ? null : quote.coupon().getCode())
                .couponApplied(quote.coupon() != null)
                .unitPrices(quote.unitPrices())
                .subtotal(quote.subtotal())
                .shippingCost(quote.shippingCost())
                .taxRate(quote.taxRate())
                .discountOnSubtotal(quote.discount().getDiscountOnSubtotal())
                .discountOnShipping(quote.discount().getDiscountOnShipping())
                .discountAmount(quote.discount().getTotalDiscount())
                .taxAmount(quote.taxAmount())
                .total(quote.total())
                .build();
    }
}
