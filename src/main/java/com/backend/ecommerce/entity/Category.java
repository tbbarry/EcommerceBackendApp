package com.backend.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private List<Category> subCategories;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<ProductCategory> productCategories;
}