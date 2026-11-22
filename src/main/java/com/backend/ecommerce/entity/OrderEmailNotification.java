package com.backend.ecommerce.entity;

import com.backend.ecommerce.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_email_notifications", indexes = {
        @Index(name = "idx_order_email_pending", columnList = "sent_at,next_attempt_at,id"),
        @Index(name = "idx_order_email_order", columnList = "order_id,id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEmailNotification extends BaseEntity {
    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(nullable = false)
    private String orderNumber;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus orderStatus;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(columnDefinition = "TEXT")
    private String itemsSnapshot;

    private Boolean adminNotification;

    @Column(columnDefinition = "TEXT")
    private String subjectSnapshot;

    @Column(columnDefinition = "TEXT")
    private String messageSnapshot;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private LocalDateTime nextAttemptAt;

    private LocalDateTime sentAt;
}
