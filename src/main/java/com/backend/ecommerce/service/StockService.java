package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.StockDto;

public interface StockService extends CrudService<StockDto, Integer> {
    void reserveStock(Integer variantId, Integer quantity, Integer orderId);
    void confirmReservations(Integer orderId);
}
