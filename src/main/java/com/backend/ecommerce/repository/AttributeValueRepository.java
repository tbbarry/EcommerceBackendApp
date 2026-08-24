package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeValueRepository
        extends JpaRepository<AttributeValue, Integer> {
}