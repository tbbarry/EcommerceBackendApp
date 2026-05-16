package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ProductDto;
import org.springframework.data.domain.Page;

public interface ProductService extends CrudService<ProductDto, Integer> {
    Page<ProductDto> findAllPaginated(int page, int size);
}
