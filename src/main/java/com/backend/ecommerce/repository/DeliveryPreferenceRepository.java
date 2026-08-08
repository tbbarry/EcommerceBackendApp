package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.DeliveryPreference;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPreferenceRepository extends JpaRepository<DeliveryPreference, Integer> {
}
