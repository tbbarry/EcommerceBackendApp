package com.example.ecommerce.service;

import com.example.ecommerce.entity.Category;
import java.util.List;

public interface CategoryService {

    Category getCategoryById(Long id);

    List<Category> getAllCategories();

    Category createCategory(Category category);

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);
}