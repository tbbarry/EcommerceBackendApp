package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Integer> {
    Optional<ProductReview> findByUserIdAndProductId(Integer userId, Integer productId);

    Page<ProductReview> findByProductIdAndVisibleTrue(Integer productId, Pageable pageable);

    @Query("select r.rating, count(r) from ProductReview r where r.product.id = :productId and r.visible = true group by r.rating")
    List<Object[]> countVisibleByRating(@Param("productId") Integer productId);
}
