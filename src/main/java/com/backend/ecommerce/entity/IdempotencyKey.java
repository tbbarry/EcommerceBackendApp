
package com.backend.ecommerce.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.backend.ecommerce.enums.IdempotencyStatus;

@Entity
@Table(name = "idempotency_keys")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class IdempotencyKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "idempotency_key",
            nullable = false,
            unique = true,
            length = 100
    )
    private String key;

    @Column(nullable = false)
    private Integer userId;

    @Column
    private Integer orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private IdempotencyStatus status;

    @Column(columnDefinition = "TEXT")
    private String response;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}

