package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.OrderEmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderEmailNotificationRepository extends JpaRepository<OrderEmailNotification, Integer> {
    @Query(value = """
            select n.* from order_email_notifications n
            where n.sent_at is null and n.next_attempt_at <= :now
              and not exists (
                  select 1 from order_email_notifications earlier
                  where earlier.order_id = n.order_id and earlier.id < n.id and earlier.sent_at is null
                    and coalesce(earlier.admin_notification, false) = coalesce(n.admin_notification, false)
              )
            order by n.id
            limit 1 for update skip locked
            """, nativeQuery = true)
    Optional<OrderEmailNotification> lockNextReady(@Param("now") LocalDateTime now);
}
