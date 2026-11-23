package com.backend.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "store_settings")
@Getter
@Setter
public class StoreSettings {
    @Id
    private Integer id = 1;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "bytea")
    private byte[] logo;

    @Column(length = 40)
    private String logoVersion;

    @Column(columnDefinition = "TEXT")
    private String aboutText;

    @Column(columnDefinition = "TEXT")
    private String contactText;

    @Column(length = 254)
    private String contactEmail;
}
