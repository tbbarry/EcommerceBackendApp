package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ShippingMethodDto;
import com.backend.ecommerce.dto.ShippingMethodRequest;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.service.ShippingMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShippingMethodServiceImpl implements ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;

    @Override
    public List<ShippingMethodDto> findActiveMethods() {
        return shippingMethodRepository.findByActiveTrueOrderByPriceAsc()
                .stream()
                .filter(method -> DeliveryType.supportedAtCheckout(method.getDeliveryType()))
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<ShippingMethodDto> findAllMethods() {
        return shippingMethodRepository.findAll(org.springframework.data.domain.Sort.by("name"))
                .stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public ShippingMethodDto createMethod(ShippingMethodRequest request) {
        return save(new ShippingMethod(), request);
    }

    @Override
    @Transactional
    public ShippingMethodDto updateMethod(Integer id, ShippingMethodRequest request) {
        ShippingMethod method = shippingMethodRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mode de livraison introuvable.", "SHIPPING_METHOD_NOT_FOUND"));
        return save(method, request);
    }

    private ShippingMethodDto save(ShippingMethod method, ShippingMethodRequest request) {
        if (request.getMaxDeliveryDays() < request.getMinDeliveryDays()) {
            throw new BusinessException("Le délai maximum doit être supérieur ou égal au délai minimum.", "INVALID_DELIVERY_DAYS");
        }
        String code = request.getCode().trim().toUpperCase(Locale.ROOT);
        shippingMethodRepository.findByCodeIgnoreCase(code).ifPresent(existing -> {
            if (!Objects.equals(existing.getId(), method.getId())) {
                throw new BusinessException("Ce code de livraison existe déjà.", "SHIPPING_CODE_EXISTS");
            }
        });
        method.setName(request.getName().trim());
        method.setCode(code);
        method.setDeliveryType(request.getDeliveryType());
        method.setDescription(request.getDescription() == null ? null : request.getDescription().trim());
        method.setMinDeliveryDays(request.getMinDeliveryDays());
        method.setMaxDeliveryDays(request.getMaxDeliveryDays());
        method.setPrice(request.getPrice());
        method.setFreeShippingThreshold(request.getFreeShippingThreshold());
        method.setActive(request.getActive());
        return toDto(shippingMethodRepository.save(method));
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
