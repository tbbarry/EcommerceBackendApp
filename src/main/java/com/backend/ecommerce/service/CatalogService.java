package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CatalogSearchRequest;
import com.backend.ecommerce.dto.CatalogProductDetailResponse;
import com.backend.ecommerce.dto.CatalogSearchResponse;

public interface CatalogService {
    CatalogSearchResponse searchProducts(CatalogSearchRequest request);

    CatalogProductDetailResponse getProductDetailsBySlug(String slug);
}
