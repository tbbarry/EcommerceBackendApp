package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.ProductCreateRequest;
import com.backend.ecommerce.dto.ProductDto;
import com.backend.ecommerce.dto.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService extends CrudService<ProductDto, Integer> {
    Page<ProductResponse> findAllPaginated(int page, int size);

    ProductResponse createProduct(ProductCreateRequest request);
}
