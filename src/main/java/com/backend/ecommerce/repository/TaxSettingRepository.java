package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.TaxSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxSettingRepository extends JpaRepository<TaxSetting, Integer> {

    Optional<TaxSetting> findByActiveTrue();
}
