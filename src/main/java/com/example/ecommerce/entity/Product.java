package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Product {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;

 private String description;

 @OneToMany(mappedBy = "product")
 private List<ProductVariant> variants;

 @OneToMany(mappedBy = "product")
 private List<ProductImage> images;

}