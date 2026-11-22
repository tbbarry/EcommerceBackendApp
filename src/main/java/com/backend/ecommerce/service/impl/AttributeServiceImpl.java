package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.AttributeDto;
import com.backend.ecommerce.dto.AttributeCreateRequest;
import com.backend.ecommerce.dto.AttributeWithValuesDto;
import com.backend.ecommerce.dto.AttributeValueDto;
import com.backend.ecommerce.dto.AttributeValueSimpleDto;
import com.backend.ecommerce.entity.Attribute;
import com.backend.ecommerce.entity.AttributeValue;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.AttributeRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final ProductImageRepository productImageRepository;
    private final CatalogProductPricingIndexService catalogProductPricingIndexService;

    @Override
    @Transactional(readOnly = true)
    public List<AttributeDto> findAll() {
        return attributeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public AttributeDto create(AttributeCreateRequest request) {
        Attribute entity = Attribute.builder()
                .name(request.getName().trim())
                .build();
        return toDto(attributeRepository.save(entity));
    }

    @Override
    public AttributeDto create(AttributeDto dto) {
        Attribute entity = new Attribute();
        entity.setName(dto.getName().trim());
        return toDto(attributeRepository.save(entity));
    }

    @Override
    public AttributeDto update(Integer id, AttributeDto dto) {
        Attribute entity = getEntityById(id);
        entity.setName(dto.getName().trim());
        Attribute saved = attributeRepository.save(entity);
        // Le nom de l'attribut est aussi le code de facette dans le catalogue.
        catalogProductPricingIndexService.refreshProducts(
            variantAttributeValueRepository.findProductIdsByAttributeId(id));
        return toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        Attribute entity = getEntityById(id);
        if (variantAttributeValueRepository.existsByAttributeValueAttributeId(id)
                || productImageRepository.existsByVisualAttributeValueAttributeId(id)) {
            throw new IllegalArgumentException("Cet attribut est utilisé par des variantes ou des images.");
        }
        attributeRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeDto> findAllWithValues() {
        return attributeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttributeWithValuesDto> getAllAttributesWithValues() {
        return attributeRepository.findAll().stream()
                .map(this::toDtoWithValues)
                .toList();
    }

    private AttributeDto toDto(Attribute entity) {
        AttributeDto dto = new AttributeDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getValues() != null) {
            dto.setValues(entity.getValues().stream()
                    .map(this::toValueDto)
                    .toList());
        }
        
        return dto;
    }

    private AttributeWithValuesDto toDtoWithValues(Attribute entity) {
        AttributeWithValuesDto dto = new AttributeWithValuesDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        
        if (entity.getValues() != null) {
            dto.setValues(entity.getValues().stream()
                    .map(this::toValueSimpleDto)
                    .toList());
        }
        
        return dto;
    }

    private AttributeValueDto toValueDto(AttributeValue entity) {
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

    private AttributeValueSimpleDto toValueSimpleDto(AttributeValue entity) {
        AttributeValueSimpleDto dto = new AttributeValueSimpleDto();
        dto.setId(entity.getId());
        dto.setValue(entity.getValue());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private Attribute getEntityById(Integer id) {
        return attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + id));
    }
}
