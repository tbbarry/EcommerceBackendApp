package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Integer> {
}
