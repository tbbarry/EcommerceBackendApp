package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM orders WHERE status = 'PAID'", nativeQuery = true)
    BigDecimal getTotalPaidOrdersAmount();
    List<Order> findAllByUser_Id(Integer userId);
}
