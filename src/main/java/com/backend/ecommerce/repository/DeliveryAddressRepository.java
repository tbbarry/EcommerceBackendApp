package com.backend.ecommerce.repository;

import com.backend.ecommerce.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Integer> {
	List<DeliveryAddress> findByDeletedFalse();

	Optional<DeliveryAddress> findByIdAndDeletedFalse(Integer id);

	List<DeliveryAddress> findByUserEmailAndDeletedFalse(String email);

	Optional<DeliveryAddress> findByIdAndUserEmailAndDeletedFalse(Integer id, String email);

	List<DeliveryAddress> findByUserIdAndDeletedFalse(Integer userId);

	List<DeliveryAddress> findByUserIdAndDefaultAddressTrueAndDeletedFalse(Integer userId);
}
