package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromoRepository extends JpaRepository<Promo, Integer> {
    Optional<Promo> findByCode(String code);
    boolean existsByCode(String code);
    Optional<Promo> findByCodeIgnoreCase(String code);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Promo p WHERE p.id = :id")
    Optional<Promo> findByIdForUpdate(@Param("id") Integer id);

    @Query("""
        SELECT p FROM Promo p
        WHERE p.isActive = true
        AND p.startDate <= :now
        AND p.endDate >= :now
    """)
    List<Promo> findAllActive(@Param("now") LocalDateTime now);
}
