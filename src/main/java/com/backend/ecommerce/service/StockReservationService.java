package com.backend.ecommerce.service;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Variant;
import java.util.List;

public interface StockReservationService {
    
    void reserveStock(Order order, Variant variant, Integer quantity);
    
    void releaseStock(Integer reservationId);
    
    List<?> findExpiredReservations();
    
    Integer processExpiredReservations();
}
