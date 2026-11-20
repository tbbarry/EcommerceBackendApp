package com.backend.ecommerce.service;

import java.util.Collection;

public interface CatalogProductPricingIndexService {

    void refreshProduct(Integer productId);

    void refreshProducts(Collection<Integer> productIds);

    void deleteProduct(Integer productId);
}
