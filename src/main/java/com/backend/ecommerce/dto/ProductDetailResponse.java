package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private Integer id;
    private String slug;
    private String name;
    private String brand;
    private String description;

    private ProductDetailPricingResponse pricing;
    private ProductDetailStockSummaryResponse stockSummary;

    private List<CatalogProductCategoryResponse> categories = new ArrayList<>();
    private List<CatalogProductColorOptionResponse> colors = new ArrayList<>();
    private List<CatalogProductSizeOptionResponse> sizes = new ArrayList<>();

    private List<ProductImageResponse> defaultImages = new ArrayList<>();
    private Map<String, List<ProductImageResponse>> imagesByColor = new LinkedHashMap<>();

    private List<ProductDetailPromotionResponse> globalPromotions = new ArrayList<>();
    private List<ProductDetailPromotionResponse> variantPromotions = new ArrayList<>();

    private List<ProductDetailVariantResponse> variants = new ArrayList<>();
    private Map<String, Map<String, CatalogVariantAvailabilityResponse>> selectionMatrix = new LinkedHashMap<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
