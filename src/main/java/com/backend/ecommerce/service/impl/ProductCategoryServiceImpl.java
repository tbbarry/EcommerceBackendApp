package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ProductCategoryDto;
import com.backend.ecommerce.entity.ProductCategory;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductCategoryRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.service.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository productCategoryRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> findAll() {
        return productCategoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductCategoryDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public ProductCategoryDto create(ProductCategoryDto dto) {
        ProductCategory entity = new ProductCategory();
        applyDtoToEntity(dto, entity);
        return toDto(productCategoryRepository.save(entity));
    }

    @Override
    public ProductCategoryDto update(Integer id, ProductCategoryDto dto) {
        ProductCategory entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(productCategoryRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        ProductCategory entity = getEntityById(id);
        productCategoryRepository.delete(entity);
    }

    private ProductCategory getEntityById(Integer id) {
        return productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductCategory not found with id: " + id));
    }

    private void applyDtoToEntity(ProductCategoryDto dto, ProductCategory entity) {
        if (dto.getProductId() != null) {
            entity.setProduct(productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId())));
        } else {
            entity.setProduct(null);
        }
        if (dto.getCategoryId() != null) {
            entity.setCategory(categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId())));
        } else {
            entity.setCategory(null);
        }
    }

    private ProductCategoryDto toDto(ProductCategory entity) {
        ProductCategoryDto dto = new ProductCategoryDto();
        dto.setId(entity.getId());
        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);
        dto.setCategoryId(entity.getCategory() != null ? entity.getCategory().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
