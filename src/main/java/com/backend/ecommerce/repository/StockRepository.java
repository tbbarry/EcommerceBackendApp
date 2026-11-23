package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.Stock;
import java.util.Optional;
import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {
     @Lock(LockModeType.PESSIMISTIC_WRITE)
     Optional<Stock> findByVariantId(Integer variantId);

     @Query("select count(v) from Variant v left join v.stock s where v.product.status = com.backend.ecommerce.entity.ProductStatus.ACTIVE and (s.id is null or s.quantity - s.reservedQuantity <= 0)")
     long countUnavailableActiveVariants();

     @Query("select v.product.id as productId, v.product.name as productName, v.sku as sku from Variant v left join v.stock s where v.product.status = com.backend.ecommerce.entity.ProductStatus.ACTIVE and (s.id is null or s.quantity - s.reservedQuantity <= 0) order by v.product.name, v.id")
     List<UnavailableVariant> findUnavailableActiveVariants(Pageable pageable);

     interface UnavailableVariant {
          Integer getProductId();
          String getProductName();
          String getSku();
     }
}
