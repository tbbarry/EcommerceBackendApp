package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Integer> {
}
