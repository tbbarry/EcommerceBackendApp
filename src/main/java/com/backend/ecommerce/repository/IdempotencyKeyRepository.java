package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyKeyRepository
        extends JpaRepository<IdempotencyKey, Long> {

    Optional<IdempotencyKey> findByKey(String key);

    Optional<IdempotencyKey> findByKeyAndUserId(
            String key,
            Integer userId
    );
}

