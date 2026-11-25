package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.*;
import com.backend.ecommerce.enums.StockReservationStatus;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.*;
import com.backend.ecommerce.service.StockReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StockReservationServiceImpl implements StockReservationService {

    private final StockReservationRepository stockReservationRepository;
    private final StockRepository stockRepository;
    private final OrderRepository orderRepository;
    private static final Integer RESERVATION_TIMEOUT_MINUTES = 15;

    @Override
    public void reserveStock(Order order, Variant variant, Integer quantity) {
        Stock stock = stockRepository.findByVariantId(variant.getId())
                .orElseThrow(() -> new BusinessException("Stock not found", "STOCK_NOT_FOUND"));

        if (stock.getAvailableQuantity() < quantity) {
            throw new BusinessException("Insufficient stock available", "INSUFFICIENT_STOCK");
        }

        stock.setReservedQuantity(stock.getReservedQuantity() + quantity);
        stockRepository.save(stock);

        StockReservation reservation = StockReservation.builder()
                .order(order)
                .variant(variant)
                .quantity(quantity)
                .status(StockReservationStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusMinutes(RESERVATION_TIMEOUT_MINUTES))
                .build();

        stockReservationRepository.save(reservation);
        log.info("Reserved {} units for order {}", quantity, order.getOrderNumber());
    }

    @Override
    public void releaseStock(Integer reservationId) {
        StockReservation reservation = stockReservationRepository.findById(reservationId)
                .orElseThrow(() -> new BusinessException("Reservation not found", "RESERVATION_NOT_FOUND"));

        Stock stock = stockRepository.findByVariantId(reservation.getVariant().getId())
                .orElseThrow(() -> new BusinessException("Stock not found", "STOCK_NOT_FOUND"));

        stock.setReservedQuantity(Math.max(0, stock.getReservedQuantity() - reservation.getQuantity()));
        stockRepository.save(stock);

        reservation.setStatus(StockReservationStatus.EXPIRED);
        stockReservationRepository.save(reservation);

        log.info("Released {} units from reservation {}", reservation.getQuantity(), reservationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> findExpiredReservations() {
        return stockReservationRepository.findExpiredReservations(LocalDateTime.now());
    }

    @Override
    public Integer processExpiredReservations() {
        List<?> expired = findExpiredReservations();
        int count = 0;

        for (Object res : expired) {
            try {
                if (res instanceof StockReservation) {
                    releaseStock(((StockReservation) res).getId());
                    count++;
                }
            } catch (Exception e) {
                log.warn("Failed to release reservation: {}", e.getMessage());
            }
        }

        log.info("Processed {} expired reservations", count);
        return count;
    }
}
