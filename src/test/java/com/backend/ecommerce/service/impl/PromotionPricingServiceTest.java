package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.VariantDiscountRow;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.DiscountType;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.service.CouponService;
import com.backend.ecommerce.service.PromotionPricingService;
import com.backend.ecommerce.service.TaxService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromotionPricingServiceTest {
    @Mock private ProductPromoRepository productPromos;
    @Mock private VariantPromoRepository variantPromos;
    @Mock private CouponService coupons;
    @Mock private TaxService taxService;
    private PromotionPricingService pricing;
    private Product product;
    private User user;

    @BeforeEach
    void setUp() {
        pricing = new PromotionPricingService(productPromos, variantPromos, coupons,
                new CouponCalculationServiceImpl(), new ShippingCalculationServiceImpl(), taxService);
        product = Product.builder().build();
        product.setId(1);
        user = new User();
    }

    @Test
    void selectsTheBestProductOrVariantDiscountWithoutCombiningThem() {
        when(productPromos.findActiveGlobalMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(new BigDecimal("20"));
        when(variantPromos.findActiveMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(List.of(new VariantDiscountRow(11, new BigDecimal("35"))));

        Map<Integer, BigDecimal> prices = pricing.promotionalUnitPrices(
                List.of(item(10, "200", 1), item(11, "100", 1)), java.time.LocalDateTime.now());

        assertEquals(new BigDecimal("160.00"), prices.get(10));
        assertEquals(new BigDecimal("65.00"), prices.get(11));
        verify(productPromos).findActiveGlobalMaxDiscountPercentByProductId(eq(1), any());
    }

    @Test
    void keepsPromotionsWhenCouponIsLessBeneficial() {
        when(productPromos.findActiveGlobalMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(new BigDecimal("20"));
        when(taxService.getCurrentTaxRate()).thenReturn(new BigDecimal("0.20"));
        Coupon coupon = coupon(DiscountType.PERCENTAGE, "10");
        when(coupons.validateCoupon(eq("CODE"), eq(user), eq(new BigDecimal("100.00")))).thenReturn(coupon);

        var quote = pricing.quote(List.of(item(10, "100", 1)), user, "CODE", shipping("0", null));

        assertNull(quote.coupon());
        assertEquals(new BigDecimal("80.00"), quote.unitPrices().get(10));
        assertEquals(new BigDecimal("96.00"), quote.total());
        assertEquals(new BigDecimal("0.00"), quote.discount().getTotalDiscount());
    }

    @Test
    void selectsCouponUsingFullTotalIncludingShippingAndTax() {
        when(productPromos.findActiveGlobalMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(new BigDecimal("20"));
        when(taxService.getCurrentTaxRate()).thenReturn(new BigDecimal("0.20"));
        Coupon coupon = coupon(DiscountType.FIXED_AMOUNT, "30");
        when(coupons.validateCoupon(eq("CODE"), eq(user), eq(new BigDecimal("100.00")))).thenReturn(coupon);

        var quote = pricing.quote(List.of(item(10, "100", 1)), user, "CODE", shipping("10", "90"));

        assertSame(coupon, quote.coupon());
        assertEquals(new BigDecimal("100.00"), quote.unitPrices().get(10));
        assertEquals(new BigDecimal("84.00"), quote.total());
        assertEquals(new BigDecimal("30.00"), quote.discount().getTotalDiscount());
    }

    @Test
    void doesNotConsumeCouponWhenOffersProduceTheSameTotal() {
        when(productPromos.findActiveGlobalMaxDiscountPercentByProductId(eq(1), any()))
                .thenReturn(new BigDecimal("20"));
        when(taxService.getCurrentTaxRate()).thenReturn(BigDecimal.ZERO);
        Coupon coupon = coupon(DiscountType.PERCENTAGE, "20");
        when(coupons.validateCoupon(eq("CODE"), eq(user), eq(new BigDecimal("100.00")))).thenReturn(coupon);

        var quote = pricing.quote(List.of(item(10, "100", 1)), user, "CODE", shipping("0", null));

        assertNull(quote.coupon());
        assertEquals(new BigDecimal("80.00"), quote.total());
    }

    private CartItem item(int id, String price, int quantity) {
        Variant variant = Variant.builder().product(product).price(new BigDecimal(price)).build();
        variant.setId(id);
        return CartItem.builder().variant(variant).quantity(quantity).build();
    }

    private Coupon coupon(DiscountType type, String value) {
        return Coupon.builder().discountType(type).discountValue(new BigDecimal(value)).build();
    }

    private ShippingMethod shipping(String price, String threshold) {
        return ShippingMethod.builder().price(new BigDecimal(price))
                .freeShippingThreshold(threshold == null ? null : new BigDecimal(threshold)).build();
    }
}
