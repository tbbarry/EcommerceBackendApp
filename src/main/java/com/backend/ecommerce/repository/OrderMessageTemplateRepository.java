package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.OrderMessageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderMessageTemplateRepository extends JpaRepository<OrderMessageTemplate, String> {}
