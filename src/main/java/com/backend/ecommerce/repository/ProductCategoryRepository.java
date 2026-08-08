package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer> {
	@Query("""
		select distinct pc
		from ProductCategory pc
		join fetch pc.category c
		left join fetch c.parentCategory
		left join fetch c.subCategories
		where pc.product.id in :productIds
		""")
	List<ProductCategory> findByProductIdIn(@Param("productIds") List<Integer> productIds);
}
