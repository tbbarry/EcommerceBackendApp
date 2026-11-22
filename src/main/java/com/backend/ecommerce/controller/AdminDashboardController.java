package com.backend.ecommerce.controller;

import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.dto.AdminOrderSummary;
import com.backend.ecommerce.service.impl.AdminOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final OrderRepository orders;
    private final StockRepository stocks;
    private final AdminOrderService adminOrders;

    @GetMapping
    @Transactional(readOnly = true)
    public Dashboard dashboard() {
        return new Dashboard(orders.countByStatus(OrderStatus.PAID),
                orders.countByStatus(OrderStatus.PROCESSING),
                stocks.countUnavailableActiveVariants(),
                adminOrders.list("", null, 0, 5).getContent(),
                stocks.findUnavailableActiveVariants(PageRequest.of(0, 5)).stream()
                        .map(v -> new UnavailableVariant(v.getProductId(), v.getProductName(), v.getSku())).toList());
    }

    public record Dashboard(long readyToPrepare, long processing, long unavailableVariants,
                            List<AdminOrderSummary> recentOrders, List<UnavailableVariant> unavailableItems) {}
    public record UnavailableVariant(Integer productId, String productName, String sku) {}
}
