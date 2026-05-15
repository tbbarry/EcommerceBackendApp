package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends BaseEntity {

    private String url;
    private String alt;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId")
    private Product product;
}