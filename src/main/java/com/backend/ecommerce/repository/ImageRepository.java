package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
}
