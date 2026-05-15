package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ImageDto;
import com.backend.ecommerce.entity.Image;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ImageRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ImageDto> findAll() {
        return imageRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ImageDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public ImageDto create(ImageDto dto) {
        Image entity = new Image();
        applyDtoToEntity(dto, entity);
        return toDto(imageRepository.save(entity));
    }

    @Override
    public ImageDto update(Integer id, ImageDto dto) {
        Image entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(imageRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Image entity = getEntityById(id);
        imageRepository.delete(entity);
    }

    private Image getEntityById(Integer id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));
    }

    private void applyDtoToEntity(ImageDto dto, Image entity) {
        entity.setUrl(dto.getUrl());
        entity.setAlt(dto.getAlt());
        if (dto.getProductId() != null) {
            entity.setProduct(productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + dto.getProductId())));
        } else {
            entity.setProduct(null);
        }
    }

    private ImageDto toDto(Image entity) {
        ImageDto dto = new ImageDto();
        dto.setId(entity.getId());
        dto.setUrl(entity.getUrl());
        dto.setAlt(entity.getAlt());
        dto.setProductId(entity.getProduct() != null ? entity.getProduct().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
