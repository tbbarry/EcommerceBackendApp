package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.StockReservation;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.enums.StockReservationStatus;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.StockReservationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class StockReservationServiceImplTest {
    @Test
    void newReservationUsesTheExistingActiveStatus() {
        StockReservationRepository reservations = mock(StockReservationRepository.class);
        StockRepository stocks = mock(StockRepository.class);
        Variant variant = new Variant();
        variant.setId(1);
        Order order = new Order();
        Stock stock = Stock.builder().quantity(5).reservedQuantity(0).build();
        when(stocks.findByVariantId(1)).thenReturn(Optional.of(stock));
        StockReservationServiceImpl service = new StockReservationServiceImpl(
                reservations, stocks, mock(OrderRepository.class));

        service.reserveStock(order, variant, 2);

        ArgumentCaptor<StockReservation> captor = ArgumentCaptor.forClass(StockReservation.class);
        verify(reservations).save(captor.capture());
        assertEquals(StockReservationStatus.ACTIVE, captor.getValue().getStatus());
        assertEquals(2, stock.getReservedQuantity());
    }
}
