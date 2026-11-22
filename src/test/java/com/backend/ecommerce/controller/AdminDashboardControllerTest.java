package com.backend.ecommerce.controller;

import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.service.impl.AdminOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AdminDashboardControllerTest {
    @Test
    void showsOrdersNeedingWorkAndUnavailableVariants() {
        OrderRepository orders = mock(OrderRepository.class);
        StockRepository stocks = mock(StockRepository.class);
        AdminOrderService adminOrders = mock(AdminOrderService.class);
        when(orders.countByStatus(OrderStatus.PAID)).thenReturn(2L);
        when(orders.countByStatus(OrderStatus.PROCESSING)).thenReturn(1L);
        when(stocks.countUnavailableActiveVariants()).thenReturn(3L);
        when(adminOrders.list("", null, 0, 5)).thenReturn(Page.empty());

        var result = new AdminDashboardController(orders, stocks, adminOrders).dashboard();
        assertEquals(2, result.readyToPrepare());
        assertEquals(1, result.processing());
        assertEquals(3, result.unavailableVariants());
        assertEquals(0, result.recentOrders().size());
    }
}
