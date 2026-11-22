package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ShippingMethodDto;
import com.backend.ecommerce.dto.ShippingMethodRequest;

import java.util.List;

public interface ShippingMethodService {

    List<ShippingMethodDto> findActiveMethods();
    List<ShippingMethodDto> findAllMethods();
    ShippingMethodDto createMethod(ShippingMethodRequest request);
    ShippingMethodDto updateMethod(Integer id, ShippingMethodRequest request);
}
