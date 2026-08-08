package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CouponPreviewRequest;
import com.backend.ecommerce.dto.CouponPreviewResponse;

public interface CouponPreviewService {

    CouponPreviewResponse preview(Integer userId, CouponPreviewRequest request);
}
