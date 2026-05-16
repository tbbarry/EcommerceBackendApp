package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.VariantDto;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.service.VariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VariantServiceImpl implements VariantService {

    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VariantDto> findAll() {
        return variantRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VariantDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public VariantDto create(VariantDto dto) {
        Variant entity = new Variant();
        applyDtoToEntity(dto, entity);
        return toDto(variantRepository.save(entity));
    }

    @Override
    public VariantDto update(Integer id, VariantDto dto) {
        Variant entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(variantRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Variant entity = getEntityById(id);
        variantRepository.delete(entity);
    }

    private Variant getEntityById(Integer id) {
        return variantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + id));
    }

    private void applyDtoToEntity(VariantDto dto, Variant entity) {
        entity.setColor(dto.getColor());
        entity.setSize(dto.getSize());
        entity.setSku(dto.getSku());
        entity.setPrice(dto.getPrice());
        if (dto.getProductId() != null) {
            entity.setProduct(productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId())));
        } else {
            entity.setProduct(null);
        }
    }

    private VariantDto toDto(Variant entity) {
        VariantDto dto = new VariantDto();
        dto.setId(entity.getId());
        dto.setColor(entity.getColor());
        dto.setSize(entity.getSize());
        dto.setSku(entity.getSku());
        dto.setPrice(entity.getPrice());
        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}