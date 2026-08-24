package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttributeRepository
        extends JpaRepository<Attribute, Integer> {
}