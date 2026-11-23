package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.AdminOrderDetail;
import com.backend.ecommerce.dto.AdminOrderStatusRequest;
import com.backend.ecommerce.dto.AdminOrderSummary;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.service.OrderService;
import com.backend.ecommerce.service.email.OrderNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminOrderService {
    private final OrderRepository repository;
    private final OrderService orders;
    private final OrderNotificationService notifications;

    @Transactional(readOnly = true)
    public Page<AdminOrderSummary> list(String search, OrderStatus status, int page, int size) {
        if (page < 0 || size < 1 || size > 100) {
            throw new BusinessException("Invalid pagination.", "INVALID_PAGINATION");
        }
        String query = search == null ? "" : search.trim();
        Specification<Order> filters = (root, criteria, builder) -> {
            var predicate = builder.conjunction();
            if (status != null) predicate = builder.and(predicate, builder.equal(root.get("status"), status));
            if (!query.isEmpty()) {
                String term = "%" + query.toLowerCase(Locale.ROOT) + "%";
                predicate = builder.and(predicate, builder.or(
                        builder.like(builder.lower(root.get("orderNumber")), term),
                        builder.like(builder.lower(root.get("user").get("email")), term),
                        builder.like(builder.lower(builder.concat(builder.concat(root.get("shippingFirstName"), " "),
                                root.get("shippingLastName"))), term)));
            }
            return predicate;
        };
        return repository.findAll(filters,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dateOrder", "id")))
                .map(order -> new AdminOrderSummary(order.getId(), order.getOrderNumber(), order.getDateOrder(),
                        order.getShippingFirstName() + " " + order.getShippingLastName(),
                        order.getUser().getEmail(), order.getTotal(), order.getStatus()));
    }

    @Transactional(readOnly = true)
    public AdminOrderDetail detail(Integer id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found.", "ORDER_NOT_FOUND"));
        return new AdminOrderDetail(orders.findById(id), order.getUser().getEmail());
    }

    @Transactional
    public AdminOrderDetail advance(Integer id, AdminOrderStatusRequest request) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found.", "ORDER_NOT_FOUND"));
        if (order.getStatus() != request.expectedStatus()) {
            throw new BusinessException("The status has changed. Refresh the order before trying again.", "ORDER_STATUS_CHANGED");
        }
        boolean valid = switch (order.getStatus()) {
            case PAID -> request.status() == OrderStatus.PROCESSING;
            case PROCESSING -> request.status() == OrderStatus.SHIPPED;
            case SHIPPED -> request.status() == OrderStatus.DELIVERED;
            default -> false;
        };
        if (!valid) {
            throw new BusinessException("This status change is not allowed.", "INVALID_ORDER_TRANSITION");
        }
        OrderStatus previousStatus = order.getStatus();
        order.setStatus(request.status());
        repository.save(order);
        notifications.queueStatusChange(order, previousStatus);
        return new AdminOrderDetail(orders.findById(id), order.getUser().getEmail());
    }
}
