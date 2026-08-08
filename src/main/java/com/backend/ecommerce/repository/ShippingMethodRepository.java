package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ShippingMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Integer> {

    List<ShippingMethod> findByActiveTrueOrderByPriceAsc();

    Optional<ShippingMethod> findByIdAndActiveTrue(Integer id);
}
