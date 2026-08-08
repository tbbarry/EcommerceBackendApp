package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	@Query("""
			select distinct c
			from Category c
			left join fetch c.subCategories
			where c.parentCategory is null
			""")
	List<Category> findRootCategoriesWithSubCategories();
}
