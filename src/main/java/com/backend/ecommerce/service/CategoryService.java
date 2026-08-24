package com.backend.ecommerce.service;

import java.util.List;

import com.backend.ecommerce.dto.CategoryDto;
import com.backend.ecommerce.dto.CategoryDto2;

public interface CategoryService extends CrudService<CategoryDto, Integer> {
    List<CategoryDto2> getCategoryTree();
}
