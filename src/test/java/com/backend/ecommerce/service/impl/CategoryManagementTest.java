package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CategoryDto;
import com.backend.ecommerce.entity.Category;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.repository.ProductCategoryRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryManagementTest {
    @Mock private CategoryRepository categories;
    @Mock private ProductRepository products;
    @Mock private ProductCategoryRepository productCategories;
    @Mock private CatalogProductPricingIndexService catalog;
    private CategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new CategoryServiceImpl(categories, products, productCategories, catalog);
    }

    @Test
    void sortsRootCategoriesByDisplayOrderThenName() {
        Category woman = category(1, "Femme", 1);
        Category man = category(2, "Homme", 2);
        Category child = category(3, "Enfant", 2);
        when(categories.findByParentCategoryIsNullOrderByName()).thenReturn(List.of(man, child, woman));

        assertEquals(List.of("Femme", "Enfant", "Homme"),
                service.getCategoryTree().stream().map(item -> item.name()).toList());
        assertEquals(1, service.getCategoryTree().get(0).displayOrder());
    }

    @Test
    void rejectsMovingCategoryBelowItsDescendant() {
        Category root = category(1, "Femme", 1);
        Category child = category(2, "Robes", 0);
        child.setParentCategory(root);
        when(categories.findById(1)).thenReturn(Optional.of(root));
        when(categories.findById(2)).thenReturn(Optional.of(child));
        CategoryDto dto = dto("Femme", 2, 1);

        assertThrows(IllegalArgumentException.class, () -> service.update(1, dto));
        verify(categories, never()).save(any());
    }

    @Test
    void preventsDeletionOfCategoriesWithChildrenOrProducts() {
        Category category = category(1, "Femme", 1);
        category.getSubCategories().add(category(2, "Robes", 0));
        when(categories.findById(1)).thenReturn(Optional.of(category));
        assertThrows(IllegalArgumentException.class, () -> service.delete(1));
        category.getSubCategories().clear();
        when(products.existsByCategoryId(1)).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.delete(1));
        verify(categories, never()).delete(any());
    }

    @Test
    void refreshesCatalogWhenCategoryNameChanges() {
        Category category = category(1, "Femme", 1);
        when(categories.findById(1)).thenReturn(Optional.of(category));
        when(categories.save(category)).thenReturn(category);
        when(products.findIdsByCategoryId(1)).thenReturn(List.of(4, 5));

        service.update(1, dto("Femmes", null, 2));

        verify(catalog).refreshProducts(List.of(4, 5));
        assertEquals(2, category.getDisplayOrder());
    }

    private Category category(int id, String name, int order) {
        Category category = Category.builder().name(name).description("Description").displayOrder(order).build();
        category.setId(id);
        return category;
    }

    private CategoryDto dto(String name, Integer parent, int order) {
        CategoryDto dto = new CategoryDto();
        dto.setName(name);
        dto.setDescription("Description");
        dto.setCategoryId(parent);
        dto.setDisplayOrder(order);
        return dto;
    }
}
