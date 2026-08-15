package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.StockReservation;
import com.backend.ecommerce.enums.StockReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockReservationRepository
        extends JpaRepository<StockReservation, Integer> {

    List<StockReservation> findByOrderId(Integer orderId);

    List<StockReservation> findByOrderIdAndStatus(
            Integer orderId,
            StockReservationStatus status
    );
}