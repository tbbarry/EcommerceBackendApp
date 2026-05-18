package com.backend.ecommerce.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.backend.ecommerce.dto.ProductDto;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public ProductDto create(ProductDto dto) {
        Product entity = new Product();
        applyDtoToEntity(dto, entity);
        return toDto(productRepository.save(entity));
    }

    @Override
    public ProductDto update(Integer id, ProductDto dto) {
        Product entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(productRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Product entity = getEntityById(id);
        productRepository.delete(entity);
    }

    private Product getEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void applyDtoToEntity(ProductDto dto, Product entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setSlug(generateSlug(entity.getBrand(), entity.getName()));
        entity.setBrand(dto.getBrand());
    }

    private ProductDto toDto(Product entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setSlug(entity.getSlug());
        dto.setBrand(entity.getBrand());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return productRepository.findAll(pageable)
                .map(this::toDto);
    }

    private String generateSlug(String brand, String name) {
        String value = brand + "_" + name;

        return java.text.Normalizer.normalize(value, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")              // enlève les accents
                .toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]+", "_")         // remplace espaces/symboles par _
                .replaceAll("^_+|_+$", "");            // enlève _ au début/fin
    }
}
