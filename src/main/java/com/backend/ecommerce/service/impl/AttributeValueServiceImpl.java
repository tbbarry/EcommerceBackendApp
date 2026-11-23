package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.AttributeValueDto;
import com.backend.ecommerce.dto.AttributeValueCreateRequest;
import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.AttributeRepository;
import com.backend.ecommerce.repository.AttributeValueRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.AttributeValueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeValueServiceImpl implements AttributeValueService {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeRepository attributeRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final ProductImageRepository productImageRepository;
    private final CatalogProductPricingIndexService catalogProductPricingIndexService;

    @Override
    @Transactional(readOnly = true)
    public List<AttributeValueDto> findAll() {
        return attributeValueRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeValueDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public AttributeValueDto create(AttributeValueCreateRequest request) {
        Attribute attribute = attributeRepository.findById(request.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + request.getAttributeId()));
        
        AttributeValue entity = AttributeValue.builder()
                .value(request.getValue().trim())
                .attribute(attribute)
                .build();
        
        return toDto(attributeValueRepository.save(entity));
    }

    @Override
    public AttributeValueDto create(AttributeValueDto dto) {
        Attribute attribute = attributeRepository.findById(dto.getAttributeId())
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + dto.getAttributeId()));
        
        AttributeValue entity = new AttributeValue();
        entity.setValue(dto.getValue().trim());
        entity.setAttribute(attribute);
        
        return toDto(attributeValueRepository.save(entity));
    }

    @Override
    public AttributeValueDto update(Integer id, AttributeValueDto dto) {
        AttributeValue entity = getEntityById(id);
        
        if (dto.getAttributeId() != null && !dto.getAttributeId().equals(entity.getAttribute().getId())) {
            Attribute attribute = attributeRepository.findById(dto.getAttributeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + dto.getAttributeId()));
            entity.setAttribute(attribute);
        }
        
        entity.setValue(dto.getValue().trim());
        
        AttributeValue saved = attributeValueRepository.save(entity);
        // Mettre a jour les facettes des produits qui utilisent cette valeur.
        catalogProductPricingIndexService.refreshProducts(
            variantAttributeValueRepository.findProductIdsByAttributeValueId(id));
        return toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        AttributeValue entity = getEntityById(id);
        if (variantAttributeValueRepository.existsByAttributeValueId(id)
                || productImageRepository.existsByVisualAttributeValueId(id)) {
            throw new IllegalArgumentException("Cette valeur est utilisée par des variantes ou des images.");
        }
        attributeValueRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeValueDto> findByAttributeId(Integer attributeId) {
        // Vérifier que l'attribut existe
        attributeRepository.findById(attributeId)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + attributeId));
        
        return attributeValueRepository.findByAttributeId(attributeId).stream()
                .map(this::toDto)
                .toList();
    }

    private AttributeValueDto toDto(AttributeValue entity) {
        AttributeValueDto dto = new AttributeValueDto();
        dto.setId(entity.getId());
        dto.setValue(entity.getValue());
        if (entity.getAttribute() != null) {
            dto.setAttributeId(entity.getAttribute().getId());
        }
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private AttributeValue getEntityById(Integer id) {
        return attributeValueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AttributeValue not found with id: " + id));
    }
}
