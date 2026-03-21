package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Inventory {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @OneToOne
 private ProductVariant variant;

 private Integer quantity;

 private Integer reserved;

}