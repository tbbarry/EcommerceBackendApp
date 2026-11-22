package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.enums.OrderStatus;
import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer>, JpaSpecificationExecutor<Order> {
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    @Query(value = "SELECT COALESCE(SUM(total), 0) FROM orders WHERE status = 'PAID'", nativeQuery = true)
    BigDecimal getTotalPaidOrdersAmount();
    List<Order> findAllByUser_Id(Integer userId);
    long countByStatus(OrderStatus status);

    @EntityGraph(attributePaths = "user")
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id = :id")
    Optional<Order> findByIdForUpdate(@Param("id") Integer id);
}
