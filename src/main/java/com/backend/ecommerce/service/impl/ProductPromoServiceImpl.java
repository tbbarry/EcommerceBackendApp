package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ProductPromoDto;
import com.backend.ecommerce.entity.ProductPromo;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductPromoRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.PromoRepository;
import com.backend.ecommerce.service.ProductPromoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductPromoServiceImpl implements ProductPromoService {

    private final ProductPromoRepository productPromoRepository;
    private final ProductRepository productRepository;
    private final PromoRepository promoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductPromoDto> findAll() {
        return productPromoRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductPromoDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public ProductPromoDto create(ProductPromoDto dto) {
        ProductPromo entity = new ProductPromo();
        applyDtoToEntity(dto, entity);
        return toDto(productPromoRepository.save(entity));
    }

    @Override
    public ProductPromoDto update(Integer id, ProductPromoDto dto) {
        ProductPromo entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(productPromoRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        ProductPromo entity = getEntityById(id);
        productPromoRepository.delete(entity);
    }

    private ProductPromo getEntityById(Integer id) {
        return productPromoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductPromo not found with id: " + id));
    }

    private void applyDtoToEntity(ProductPromoDto dto, ProductPromo entity) {
        if (dto.getProductId() != null) {
            entity.setProduct(productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId())));
        } else {
            entity.setProduct(null);
        }
        if (dto.getPromoId() != null) {
            entity.setPromo(promoRepository.findById(dto.getPromoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Promo not found with id: " + dto.getPromoId())));
        } else {
            entity.setPromo(null);
        }
    }

    private ProductPromoDto toDto(ProductPromo entity) {
        ProductPromoDto dto = new ProductPromoDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);
        dto.setPromoId(entity.getPromo() != null ? entity.getPromo().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
