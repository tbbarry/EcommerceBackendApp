package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	@Query("""
			select distinct c
			from Category c
			left join fetch c.subCategories
			where c.parentCategory is null
			""")
	List<Category> findRootCategoriesWithSubCategories();
	List<Category> findByParentCategoryIsNullOrderByName();
	List<Category> findByParentCategoryIdOrderByName(Integer parentId);
	@Query(value = """
    WITH RECURSIVE category_tree AS (
        SELECT id
        FROM categories
        WHERE id = :categoryId

        UNION ALL

        SELECT c.id
        FROM categories c
        INNER JOIN category_tree ct
            ON c.category_id = ct.id
    )
    SELECT id
    FROM category_tree
    """, nativeQuery = true)
	List<Long> findCategoryTreeIds(@Param("categoryId") Long categoryId);
}
