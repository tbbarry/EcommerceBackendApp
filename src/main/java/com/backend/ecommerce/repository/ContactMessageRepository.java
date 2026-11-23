package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ContactMessage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Integer> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from ContactMessage m where m.id = :id")
    Optional<ContactMessage> findForReply(@Param("id") Integer id);
}
