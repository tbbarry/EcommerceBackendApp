package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Stock;
import java.util.Optional;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<Stock> findByVariantId(Integer variantId);
}
