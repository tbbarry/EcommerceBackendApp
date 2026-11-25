package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.StockReservation;
import com.backend.ecommerce.enums.StockReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StockReservationRepository
        extends JpaRepository<StockReservation, Integer> {

    List<StockReservation> findByOrderId(Integer orderId);

    List<StockReservation> findByOrderIdAndStatus(
            Integer orderId,
            StockReservationStatus status
    );

    @Query("SELECT sr FROM StockReservation sr WHERE sr.expiresAt <= :now AND sr.status = com.backend.ecommerce.enums.StockReservationStatus.ACTIVE")
    List<StockReservation> findExpiredReservations(@Param("now") LocalDateTime now);
}