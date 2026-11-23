package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.dto.VariantDiscountRow;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionPricingService {
    private final ProductPromoRepository productPromos;
    private final VariantPromoRepository variantPromos;
    private final CouponService couponService;
    private final CouponCalculationService couponCalculation;
    private final ShippingCalculationService shippingCalculation;
    private final TaxService taxService;

    public record Quote(Map<Integer, BigDecimal> unitPrices, BigDecimal subtotal, BigDecimal shippingCost,
                        BigDecimal effectiveShipping, BigDecimal taxRate, BigDecimal taxAmount, BigDecimal total,
                        Coupon coupon, CouponDiscountResult discount) {}

    private record ProductDiscounts(BigDecimal global, Map<Integer, BigDecimal> variants) {}

    public Map<Integer, BigDecimal> promotionalUnitPrices(List<CartItem> items, LocalDateTime now) {
        Map<Integer, ProductDiscounts> discounts = new HashMap<>();
        Map<Integer, BigDecimal> prices = new HashMap<>();
        for (CartItem item : items) {
            Variant variant = item.getVariant();
            Integer productId = variant.getProduct().getId();
            ProductDiscounts productDiscounts = discounts.computeIfAbsent(productId, id -> new ProductDiscounts(
                    normalize(productPromos.findActiveGlobalMaxDiscountPercentByProductId(id, now)),
                    variantPromos.findActiveMaxDiscountPercentByProductId(id, now).stream()
                            .collect(Collectors.toMap(VariantDiscountRow::variantId,
                                    row -> normalize(row.discountPercent()), BigDecimal::max))));
            BigDecimal percent = productDiscounts.global().max(
                    productDiscounts.variants().getOrDefault(variant.getId(), BigDecimal.ZERO));
            BigDecimal ratio = BigDecimal.ONE.subtract(percent.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
            prices.put(variant.getId(), variant.getPrice().multiply(ratio).setScale(2, RoundingMode.HALF_UP));
        }
        return prices;
    }

    public Quote quote(List<CartItem> items, User user, String couponCode, ShippingMethod shippingMethod) {
        Map<Integer, BigDecimal> promotionalPrices = promotionalUnitPrices(items, LocalDateTime.now());
        Map<Integer, BigDecimal> basePrices = items.stream().collect(Collectors.toMap(
                item -> item.getVariant().getId(),
                item -> item.getVariant().getPrice().setScale(2, RoundingMode.HALF_UP),
                (first, duplicate) -> first));
        BigDecimal taxRate = taxService.getCurrentTaxRate();
        BigDecimal promotionalSubtotal = subtotal(items, promotionalPrices);
        Quote promotionalQuote = createQuote(promotionalPrices, promotionalSubtotal, shippingMethod, taxRate, null);
        if (couponCode == null || couponCode.isBlank()) {
            return promotionalQuote;
        }

        BigDecimal baseSubtotal = subtotal(items, basePrices);
        Coupon coupon = couponService.validateCoupon(couponCode, user, baseSubtotal);
        Quote couponQuote = createQuote(basePrices, baseSubtotal, shippingMethod, taxRate, coupon);
        // Compare complete totals: shipping thresholds and tax can change the best offer.
        return couponQuote.total().compareTo(promotionalQuote.total()) < 0 ? couponQuote : promotionalQuote;
    }

    private Quote createQuote(Map<Integer, BigDecimal> prices, BigDecimal subtotal, ShippingMethod shippingMethod,
                              BigDecimal taxRate, Coupon coupon) {
        BigDecimal shipping = shippingMethod == null ? BigDecimal.ZERO
                : shippingCalculation.calculateShippingCost(subtotal, shippingMethod).setScale(2, RoundingMode.HALF_UP);
        CouponDiscountResult discount = couponCalculation.calculateDiscount(coupon, subtotal, shipping);
        BigDecimal taxableSubtotal = subtotal.subtract(discount.getDiscountOnSubtotal()).max(BigDecimal.ZERO);
        BigDecimal effectiveShipping = shipping.subtract(discount.getDiscountOnShipping()).max(BigDecimal.ZERO);
        BigDecimal tax = taxableSubtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = taxableSubtotal.add(effectiveShipping).add(tax).setScale(2, RoundingMode.HALF_UP);
        return new Quote(prices, subtotal, shipping, effectiveShipping, taxRate, tax, total, coupon, discount);
    }

    private BigDecimal subtotal(List<CartItem> items, Map<Integer, BigDecimal> prices) {
        return items.stream().map(item -> prices.get(item.getVariant().getId())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalize(BigDecimal percent) {
        return percent == null ? BigDecimal.ZERO : percent.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
