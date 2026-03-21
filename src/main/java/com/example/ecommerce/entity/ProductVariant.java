package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Data
public class ProductVariant {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String sku;

 private BigDecimal price;

 @ManyToOne
 private Product product;


@OneToMany(mappedBy = "variant")
private List<ProductImage> images;

}