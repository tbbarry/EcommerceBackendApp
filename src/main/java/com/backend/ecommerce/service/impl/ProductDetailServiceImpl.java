package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CatalogProductCategoryResponse;
import com.backend.ecommerce.dto.CatalogProductColorOptionResponse;
import com.backend.ecommerce.dto.CatalogProductSizeOptionResponse;
import com.backend.ecommerce.dto.CatalogVariantAvailabilityResponse;
import com.backend.ecommerce.dto.ProductDetailPricingResponse;
import com.backend.ecommerce.dto.ProductDetailPromotionResponse;
import com.backend.ecommerce.dto.ProductDetailResponse;
import com.backend.ecommerce.dto.ProductDetailStockSummaryResponse;
import com.backend.ecommerce.dto.ProductDetailVariantResponse;
import com.backend.ecommerce.dto.ProductImageResponse;
import com.backend.ecommerce.entity.Category;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.ProductPromo;
import com.backend.ecommerce.entity.Promo;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.VariantPromo;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.VariantPromoRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.ProductDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductDetailServiceImpl implements ProductDetailService {

    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductPromoRepository productPromoRepository;
    private final VariantPromoRepository variantPromoRepository;

    @Override
    public ProductDetailResponse getBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with slug: " + slug,
                        "PRODUCT_NOT_FOUND"
                ));

        List<Variant> variants = variantRepository.findDetailedByProductId(product.getId());
        List<ProductImage> images = productImageRepository.findByProductId(product.getId());
        LocalDateTime now = LocalDateTime.now();

        List<ProductPromo> activeProductPromos = productPromoRepository.findActiveByProductId(product.getId(), now);
        List<VariantPromo> activeVariantPromos = variantPromoRepository.findActiveByProductId(product.getId(), now);

        BigDecimal globalDiscountPercent = activeProductPromos.stream()
                .map(ProductPromo::getPromo)
                .filter(Objects::nonNull)
                .map(Promo::getDiscountValue)
                .filter(Objects::nonNull)
                .map(this::normalizePercent)
                .max(Comparator.naturalOrder())
                .orElse(ZERO);

        Map<Integer, BigDecimal> variantDiscountMap = activeVariantPromos.stream()
                .filter(vp -> vp.getVariant() != null && vp.getPromo() != null)
                .collect(Collectors.toMap(
                        vp -> vp.getVariant().getId(),
                        vp -> normalizePercent(vp.getPromo().getDiscountValue()),
                        BigDecimal::max,
                        LinkedHashMap::new
                ));

        Map<Integer, List<ProductDetailPromotionResponse>> variantPromotionsByVariant = activeVariantPromos.stream()
                .filter(vp -> vp.getVariant() != null && vp.getPromo() != null)
                .collect(Collectors.groupingBy(
                        vp -> vp.getVariant().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(this::toVariantPromotionResponse, Collectors.toList())
                ));

        List<ProductDetailPromotionResponse> globalPromotions = activeProductPromos.stream()
                .filter(pp -> pp.getPromo() != null)
                .map(this::toGlobalPromotionResponse)
                .toList();

        Map<String, List<ProductImageResponse>> imagesByColor = buildImagesByColor(images);
        List<ProductImageResponse> defaultImages = buildDefaultImages(images);
        List<CatalogProductCategoryResponse> categories = buildCategories(product.getCategory());

        List<ProductDetailVariantResponse> variantResponses = new ArrayList<>();
        List<ProductDetailPromotionResponse> variantPromotions = new ArrayList<>();
        Map<String, ColorAccumulator> colorAccumulators = new LinkedHashMap<>();
        Map<String, SizeAccumulator> sizeAccumulators = new LinkedHashMap<>();
        Map<String, Map<String, CatalogVariantAvailabilityResponse>> selectionMatrix = new LinkedHashMap<>();

        BigDecimal baseMin = null;
        BigDecimal baseMax = null;
        BigDecimal finalMin = null;
        BigDecimal finalMax = null;
        BigDecimal discountMin = null;
        BigDecimal discountMax = null;
        int totalStock = 0;
        int inStockVariants = 0;
        int outOfStockVariants = 0;
        boolean hasVariantSpecificPromo = false;

        for (Variant variant : variants) {
            VariantDescriptor descriptor = VariantDescriptor.from(variant);
            int stock = resolveAvailableStock(variant);
            boolean inStock = stock > 0;

            BigDecimal basePrice = safeMoney(variant.getPrice());
            BigDecimal variantSpecificDiscount = variantDiscountMap.getOrDefault(variant.getId(), ZERO);
            BigDecimal appliedDiscount = globalDiscountPercent.max(variantSpecificDiscount);
            BigDecimal finalPrice = computeFinalPrice(basePrice, appliedDiscount);

            if (variantSpecificDiscount.compareTo(globalDiscountPercent) > 0) {
                hasVariantSpecificPromo = true;
            }

            totalStock += stock;
            if (inStock) {
                inStockVariants++;
            } else {
                outOfStockVariants++;
            }

            baseMin = min(baseMin, basePrice);
            baseMax = max(baseMax, basePrice);
            finalMin = min(finalMin, finalPrice);
            finalMax = max(finalMax, finalPrice);
            discountMin = min(discountMin, appliedDiscount);
            discountMax = max(discountMax, appliedDiscount);

            List<Integer> imageIds = resolveImageIdsForColor(descriptor.color(), images);

            ProductDetailVariantResponse variantResponse = new ProductDetailVariantResponse();
            variantResponse.setId(variant.getId());
            variantResponse.setSku(variant.getSku());
            variantResponse.setColor(descriptor.color());
            variantResponse.setSize(descriptor.size());
            variantResponse.setPrice(basePrice);
            variantResponse.setFinalPrice(finalPrice);
            variantResponse.setAppliedDiscountPercent(appliedDiscount);
            variantResponse.setAppliedPromoScope(resolveVariantPromoScope(globalDiscountPercent, variantSpecificDiscount));
            variantResponse.setStock(stock);
            variantResponse.setInStock(inStock);
            variantResponse.setImageIds(imageIds);
            variantResponses.add(variantResponse);

            variantPromotions.addAll(variantPromotionsByVariant.getOrDefault(variant.getId(), List.of()));

            if (descriptor.color() != null && !descriptor.color().isBlank()) {
                ColorAccumulator colorAccumulator = colorAccumulators.computeIfAbsent(
                        descriptor.color(),
                        key -> new ColorAccumulator(key, imagesByColor.getOrDefault(key, List.of()).size())
                );
                colorAccumulator.totalStock += stock;
                colorAccumulator.available |= inStock;
                if (descriptor.size() != null && !descriptor.size().isBlank()) {
                    colorAccumulator.sizesAvailable.add(descriptor.size());
                }
            }

            if (descriptor.size() != null && !descriptor.size().isBlank()) {
                SizeAccumulator sizeAccumulator = sizeAccumulators.computeIfAbsent(
                        descriptor.size(),
                        key -> new SizeAccumulator(key)
                );
                sizeAccumulator.totalStock += stock;
                sizeAccumulator.available |= inStock;
            }

            if (descriptor.color() != null && !descriptor.color().isBlank()
                    && descriptor.size() != null && !descriptor.size().isBlank()) {
                selectionMatrix
                        .computeIfAbsent(descriptor.color(), key -> new LinkedHashMap<>())
                        .put(
                                descriptor.size(),
                                new CatalogVariantAvailabilityResponse(variant.getId(), stock, inStock)
                        );
            }
        }

        if (variants.isEmpty()) {
            baseMin = safeMoney(product.getPrice());
            baseMax = baseMin;
            finalMin = computeFinalPrice(baseMin, globalDiscountPercent);
            finalMax = finalMin;
            discountMin = globalDiscountPercent;
            discountMax = globalDiscountPercent;
        }

        ProductDetailPricingResponse pricing = new ProductDetailPricingResponse(
                safeMoney(baseMin),
                safeMoney(baseMax),
                safeMoney(finalMin),
                safeMoney(finalMax),
                safeMoney(finalMin),
                defaultPercent(discountMax).compareTo(BigDecimal.ZERO) > 0,
                resolveProductPromoScope(defaultPercent(discountMax), hasVariantSpecificPromo),
                defaultPercent(globalDiscountPercent),
                defaultPercent(discountMin),
                defaultPercent(discountMax)
        );

        ProductDetailStockSummaryResponse stockSummary = new ProductDetailStockSummaryResponse(
                totalStock,
                inStockVariants,
                outOfStockVariants,
                totalStock > 0
        );

        ProductDetailResponse response = new ProductDetailResponse();
        response.setId(product.getId());
        response.setSlug(product.getSlug());
        response.setName(product.getName());
        response.setBrand(product.getBrand());
        response.setDescription(product.getDescription());
        response.setPricing(pricing);
        response.setStockSummary(stockSummary);
        response.setCategories(categories);
        response.setColors(buildColorResponses(colorAccumulators.values()));
        response.setSizes(buildSizeResponses(sizeAccumulators.values()));
        response.setDefaultImages(defaultImages);
        response.setImagesByColor(imagesByColor);
        response.setGlobalPromotions(globalPromotions);
        response.setVariantPromotions(variantPromotions);
        response.setVariants(variantResponses);
        response.setSelectionMatrix(selectionMatrix);
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }

    private ProductDetailPromotionResponse toGlobalPromotionResponse(ProductPromo productPromo) {
        Promo promo = productPromo.getPromo();
        return new ProductDetailPromotionResponse(
                null,
                promo.getCode(),
                normalizePercent(promo.getDiscountValue()),
                "GLOBAL"
        );
    }

    private ProductDetailPromotionResponse toVariantPromotionResponse(VariantPromo variantPromo) {
        Promo promo = variantPromo.getPromo();
        return new ProductDetailPromotionResponse(
                variantPromo.getVariant().getId(),
                promo.getCode(),
                normalizePercent(promo.getDiscountValue()),
                "VARIANT"
        );
    }

    private List<CatalogProductCategoryResponse> buildCategories(Category leaf) {
        List<CatalogProductCategoryResponse> categories = new ArrayList<>();
        Category current = leaf;
        while (current != null) {
            Category parent = current.getParentCategory();
            categories.add(0, new CatalogProductCategoryResponse(
                    current.getId(),
                    current.getName(),
                    parent != null ? parent.getId() : null,
                    parent != null ? parent.getName() : null
            ));
            current = parent;
        }
        return categories;
    }

    private List<ProductImageResponse> buildDefaultImages(List<ProductImage> images) {
        return images.stream()
                .filter(image -> image.getVisualAttributeValue() == null)
                .sorted(Comparator
                        .comparing(ProductImage::isMain).reversed()
                        .thenComparing(ProductImage::getDisplayOrder)
                        .thenComparing(ProductImage::getId))
                .map(this::toImageResponse)
                .toList();
    }

    private Map<String, List<ProductImageResponse>> buildImagesByColor(List<ProductImage> images) {
        return images.stream()
                .filter(image -> image.getVisualAttributeValue() != null)
                .collect(Collectors.groupingBy(
                        image -> image.getVisualAttributeValue().getValue(),
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(Collectors.toList(), list -> list.stream()
                                .sorted(Comparator
                                        .comparing(ProductImage::isMain).reversed()
                                        .thenComparing(ProductImage::getDisplayOrder)
                                        .thenComparing(ProductImage::getId))
                                .map(this::toImageResponse)
                                .toList())
                ));
    }

    private ProductImageResponse toImageResponse(ProductImage image) {
        return new ProductImageResponse(
                image.getId(),
                image.getUrl(),
                image.getAlt(),
                image.isMain(),
                image.getVisualAttributeValue() != null ? image.getVisualAttributeValue().getValue() : null
        );
    }

    private List<Integer> resolveImageIdsForColor(String color, List<ProductImage> images) {
        if (color == null || color.isBlank()) {
            return List.of();
        }

        return images.stream()
                .filter(image -> image.getVisualAttributeValue() != null)
                .filter(image -> color.equalsIgnoreCase(image.getVisualAttributeValue().getValue()))
                .map(ProductImage::getId)
                .toList();
    }

    private int resolveAvailableStock(Variant variant) {
        if (variant.getStock() == null || variant.getStock().getAvailableQuantity() == null) {
            return 0;
        }
        return variant.getStock().getAvailableQuantity();
    }

    private BigDecimal computeFinalPrice(BigDecimal basePrice, BigDecimal discountPercent) {
        BigDecimal normalizedDiscount = defaultPercent(discountPercent);
        if (normalizedDiscount.compareTo(BigDecimal.ZERO) <= 0) {
            return safeMoney(basePrice);
        }

        BigDecimal ratio = BigDecimal.ONE.subtract(
                normalizedDiscount.divide(HUNDRED, 6, RoundingMode.HALF_UP)
        );
        return safeMoney(basePrice).multiply(ratio).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal normalizePercent(BigDecimal percent) {
        if (percent == null) {
            return ZERO;
        }
        if (percent.compareTo(BigDecimal.ZERO) < 0) {
            return ZERO;
        }
        if (percent.compareTo(HUNDRED) > 0) {
            return HUNDRED.setScale(2, RoundingMode.HALF_UP);
        }
        return percent.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal safeMoney(BigDecimal value) {
        if (value == null) {
            return ZERO;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultPercent(BigDecimal value) {
        return normalizePercent(value);
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

    private String resolveVariantPromoScope(BigDecimal globalDiscount, BigDecimal variantDiscount) {
        BigDecimal safeGlobal = defaultPercent(globalDiscount);
        BigDecimal safeVariant = defaultPercent(variantDiscount);
        if (safeVariant.compareTo(safeGlobal) > 0) {
            return "VARIANT";
        }
        if (safeGlobal.compareTo(BigDecimal.ZERO) > 0) {
            return "GLOBAL";
        }
        return "NONE";
    }

    private String resolveProductPromoScope(BigDecimal discountMax, boolean hasVariantSpecificPromo) {
        if (defaultPercent(discountMax).compareTo(BigDecimal.ZERO) <= 0) {
            return "NONE";
        }
        return hasVariantSpecificPromo ? "VARIANT" : "GLOBAL";
    }

    private List<CatalogProductColorOptionResponse> buildColorResponses(Collection<ColorAccumulator> accumulators) {
        return accumulators.stream()
                .map(acc -> new CatalogProductColorOptionResponse(
                        acc.name,
                        acc.available,
                        acc.totalStock,
                        acc.imageCount,
                        new ArrayList<>(acc.sizesAvailable)
                ))
                .toList();
    }

    private List<CatalogProductSizeOptionResponse> buildSizeResponses(Collection<SizeAccumulator> accumulators) {
        return accumulators.stream()
                .map(acc -> new CatalogProductSizeOptionResponse(
                        acc.value,
                        acc.available,
                        acc.totalStock
                ))
                .toList();
    }

    private static final class ColorAccumulator {
        private final String name;
        private boolean available;
        private int totalStock;
        private final int imageCount;
        private final Set<String> sizesAvailable = new LinkedHashSet<>();

        private ColorAccumulator(String name, int imageCount) {
            this.name = name;
            this.imageCount = imageCount;
        }
    }

    private static final class SizeAccumulator {
        private final String value;
        private boolean available;
        private int totalStock;

        private SizeAccumulator(String value) {
            this.value = value;
        }
    }
}
