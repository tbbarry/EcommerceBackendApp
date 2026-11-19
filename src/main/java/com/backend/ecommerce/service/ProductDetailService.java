package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ProductDetailResponse;

public interface ProductDetailService {

    ProductDetailResponse getBySlug(String slug);
}
