package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ShippingMethodDto;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.service.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShippingMethodServiceImpl implements ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;

    @Override
    public List<ShippingMethodDto> findActiveMethods() {
        return shippingMethodRepository.findByActiveTrueOrderByPriceAsc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ShippingMethodDto toDto(ShippingMethod entity) {
        return ShippingMethodDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .deliveryType(entity.getDeliveryType())
                .description(entity.getDescription())
                .minDeliveryDays(entity.getMinDeliveryDays())
                .maxDeliveryDays(entity.getMaxDeliveryDays())
                .price(entity.getPrice())
                .freeShippingThreshold(entity.getFreeShippingThreshold())
                .active(entity.isActive())
                .build();
    }
}
