package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CategoryDto;
import com.backend.ecommerce.dto.CategoryDto2;
import com.backend.ecommerce.entity.Category;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public CategoryDto create(CategoryDto dto) {
        Category entity = new Category();
        applyDtoToEntity(dto, entity);
        return toDto(categoryRepository.save(entity));
    }

    @Override
    public CategoryDto update(Integer id, CategoryDto dto) {
        Category entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(categoryRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Category entity = getEntityById(id);
        categoryRepository.delete(entity);
    }

    @Override
    public List<CategoryDto2> getCategoryTree() {

        return categoryRepository
                .findByParentCategoryIsNullOrderByName()
                .stream()
                .map(this::toDto2)
                .toList();
    }

    private CategoryDto2 toDto2(Category category) {

        List<CategoryDto2> children = category.getSubCategories()
                .stream()
                .sorted(Comparator.comparing(Category::getName))
                .map(this::toDto2)
                .toList();

        return new CategoryDto2(
                Long.valueOf(category.getId()),
                category.getName(),
                children
        );
    }

    private Category getEntityById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    private void applyDtoToEntity(CategoryDto dto, Category entity) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        if (dto.getCategoryId() != null) {
            entity.setParentCategory(categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId())));
        } else {
            entity.setParentCategory(null);
        }
    }

    private CategoryDto toDto(Category entity) {
        CategoryDto dto = new CategoryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setCategoryId(entity.getParentCategory() != null ? entity.getParentCategory().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    
}



