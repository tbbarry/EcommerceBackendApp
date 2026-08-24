package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.CatalogResponse;
import com.backend.ecommerce.dto.CatalogSearchRequest;

public interface CatalogService2 {

    CatalogResponse search(CatalogSearchRequest request);

}