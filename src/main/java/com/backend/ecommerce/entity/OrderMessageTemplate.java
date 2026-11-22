package com.backend.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_message_templates")
@Getter
@Setter
@NoArgsConstructor
public class OrderMessageTemplate {
    @Id
    @Column(length = 30)
    private String templateKey;

    @Column(nullable = false, length = 200)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    public OrderMessageTemplate(String templateKey, String subject, String message) {
        this.templateKey = templateKey;
        this.subject = subject;
        this.message = message;
    }
}
