package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ShippingMethodDto;

import java.util.List;

public interface ShippingMethodService {

    List<ShippingMethodDto> findActiveMethods();
}
