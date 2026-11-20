package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.VariantDiscountRow;
import com.backend.ecommerce.entity.CatalogProduct;
import com.backend.ecommerce.entity.CatalogProductFacet;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.repository.CatalogFacetRepository;
import com.backend.ecommerce.repository.CatalogProductRepository;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CatalogProductPricingIndexServiceImpl implements CatalogProductPricingIndexService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private final VariantRepository variantRepository;
    private final ProductPromoRepository productPromoRepository;
    private final VariantPromoRepository variantPromoRepository;
    private final CatalogProductRepository catalogProductRepository;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final CatalogFacetRepository catalogFacetRepository;

    @Override
    public void refreshProduct(Integer productId) {
        if (productId == null) {
            return;
        }

        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return;
        }
        // La fiche de recherche est creee au premier indexage, puis reutilisee.
        CatalogProduct catalogProduct = catalogProductRepository.findByProductId(productId.longValue())
            .orElseGet(() -> CatalogProduct.builder().productId(productId.longValue()).build());
        catalogProduct.setName(product.getName());
        catalogProduct.setSlug(product.getSlug());
        catalogProduct.setBrand(product.getBrand());
        catalogProduct.setCategoryId(product.getCategory() == null ? null : product.getCategory().getId().longValue());
        catalogProduct.setCategoryName(product.getCategory() == null ? null : product.getCategory().getName());
        catalogProduct.setImageUrl(productImageRepository.findByProductId(productId).stream()
            .sorted(Comparator.comparing(ProductImage::isMain).reversed()
                .thenComparing(ProductImage::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ProductImage::getId))
            .map(ProductImage::getUrl).findFirst().orElse(null));

        // Une valeur utilisee par plusieurs variantes ne doit apparaitre qu'une fois.
        catalogFacetRepository.deleteByProductId(productId.longValue());
        Map<Integer, AttributeValue> values = variantAttributeValueRepository.findDetailedByProductId(productId)
            .stream().map(association -> association.getAttributeValue())
            .collect(Collectors.toMap(AttributeValue::getId, value -> value, (first, duplicate) -> first));
        catalogFacetRepository.saveAll(values.values().stream().map(value -> CatalogProductFacet.builder()
            .productId(productId.longValue())
            .facetId(value.getAttribute().getId().longValue())
            .facetCode(value.getAttribute().getName())
            .facetValueId(value.getId().longValue())
            .valueCode(value.getValue())
            .valueLabel(value.getValue())
            .build()).toList());

        LocalDateTime now = LocalDateTime.now();
        BigDecimal globalDiscount = defaultPercent(
            productPromoRepository.findActiveGlobalMaxDiscountPercentByProductId(productId, now)
        );

        List<Variant> variants = variantRepository.findByProductId(productId);
        if (variants == null || variants.isEmpty()) {
            catalogProduct.setPrice(product.getPrice());
            applyNoVariantPricing(catalogProduct, globalDiscount);
            catalogProductRepository.save(catalogProduct);
            return;
        }

        // Pour chaque variante, la plus forte remise active l'emporte.
        Map<Integer, BigDecimal> variantDiscountMap = variantPromoRepository
                .findActiveMaxDiscountPercentByProductId(productId, now)
                .stream()
                .collect(Collectors.toMap(
                        VariantDiscountRow::variantId,
                        row -> defaultPercent(row.discountPercent()),
                        BigDecimal::max
                ));

        BigDecimal baseMin = null;
        BigDecimal baseMax = null;
        BigDecimal finalMin = null;
        BigDecimal finalMax = null;
        BigDecimal discountMin = null;
        BigDecimal discountMax = null;

        boolean hasVariantSpecificPromo = false;

        for (Variant variant : variants) {
            BigDecimal basePrice = safeMoney(variant.getPrice());
            BigDecimal variantSpecific = defaultPercent(variantDiscountMap.get(variant.getId()));

            if (variantSpecific.compareTo(BigDecimal.ZERO) > 0) {
                hasVariantSpecificPromo = true;
            }

            BigDecimal appliedPercent = globalDiscount.max(variantSpecific);
            BigDecimal finalPrice = computeFinalPrice(basePrice, appliedPercent);

            baseMin = min(baseMin, basePrice);
            baseMax = max(baseMax, basePrice);
            finalMin = min(finalMin, finalPrice);
            finalMax = max(finalMax, finalPrice);
            discountMin = min(discountMin, appliedPercent);
            discountMax = max(discountMax, appliedPercent);
        }

        BigDecimal safeBaseMin = safeMoney(baseMin);
        BigDecimal safeBaseMax = safeMoney(baseMax);
        BigDecimal safeFinalMin = safeMoney(finalMin);
        BigDecimal safeFinalMax = safeMoney(finalMax);
        BigDecimal safeDiscountMin = defaultPercent(discountMin);
        BigDecimal safeDiscountMax = defaultPercent(discountMax);

        boolean hasDiscount = safeDiscountMax.compareTo(BigDecimal.ZERO) > 0;

        catalogProduct.setBasePriceMin(safeBaseMin);
        catalogProduct.setBasePriceMax(safeBaseMax);
        catalogProduct.setFinalPriceMin(safeFinalMin);
        catalogProduct.setFinalPriceMax(safeFinalMax);
        catalogProduct.setDisplayPrice(safeFinalMin);
        catalogProduct.setPrice(safeFinalMin);
        catalogProduct.setDiscountPercentGlobal(defaultPercent(globalDiscount));
        catalogProduct.setDiscountPercentMin(safeDiscountMin);
        catalogProduct.setDiscountPercentMax(safeDiscountMax);
        catalogProduct.setHasDiscount(hasDiscount);
        catalogProduct.setPromoScope(resolvePromoScope(hasDiscount, hasVariantSpecificPromo));

        catalogProductRepository.save(catalogProduct);
    }

    @Override
    public void refreshProducts(Collection<Integer> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        Set<Integer> uniqueIds = productIds.stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        for (Integer productId : uniqueIds) {
            refreshProduct(productId);
        }
    }

    @Override
    public void deleteProduct(Integer productId) {
        if (productId != null) {
            // Supprimer aussi les lignes de recherche qui n'ont pas de cle etrangere.
            catalogFacetRepository.deleteByProductId(productId.longValue());
            catalogProductRepository.deleteByProductId(productId.longValue());
        }
    }

    private void applyNoVariantPricing(CatalogProduct catalogProduct, BigDecimal globalDiscount) {
        BigDecimal fallbackPrice = safeMoney(catalogProduct.getPrice());
        BigDecimal finalPrice = computeFinalPrice(fallbackPrice, globalDiscount);
        boolean hasDiscount = globalDiscount.compareTo(BigDecimal.ZERO) > 0;
        catalogProduct.setBasePriceMin(fallbackPrice);
        catalogProduct.setBasePriceMax(fallbackPrice);
        catalogProduct.setFinalPriceMin(finalPrice);
        catalogProduct.setFinalPriceMax(finalPrice);
        catalogProduct.setDisplayPrice(finalPrice);
        catalogProduct.setPrice(finalPrice);
        catalogProduct.setDiscountPercentGlobal(globalDiscount);
        catalogProduct.setDiscountPercentMin(globalDiscount);
        catalogProduct.setDiscountPercentMax(globalDiscount);
        catalogProduct.setHasDiscount(hasDiscount);
        catalogProduct.setPromoScope(hasDiscount ? "GLOBAL" : "NONE");
    }

    private BigDecimal computeFinalPrice(BigDecimal basePrice, BigDecimal discountPercent) {
        if (discountPercent.compareTo(BigDecimal.ZERO) <= 0) {
            return safeMoney(basePrice);
        }

        BigDecimal ratio = BigDecimal.ONE.subtract(
                discountPercent.divide(HUNDRED, 6, RoundingMode.HALF_UP)
        );

        return basePrice.multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeMoney(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultPercent(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }

        if (value.compareTo(HUNDRED) > 0) {
            return HUNDRED.setScale(2, RoundingMode.HALF_UP);
        }

        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal min(BigDecimal left, BigDecimal right) {
        if (left == null) {
            return right;
        }
        return left.min(right);
    }

    private BigDecimal max(BigDecimal left, BigDecimal right) {
        if (left == null) {
            return right;
        }
        return left.max(right);
    }

    private String resolvePromoScope(boolean hasDiscount, boolean hasVariantSpecificPromo) {
        if (!hasDiscount) {
            return "NONE";
        }

        return hasVariantSpecificPromo ? "VARIANT" : "GLOBAL";
    }
}
