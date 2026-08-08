package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.DeliveryAddressDto;
import com.backend.ecommerce.dto.MyDeliveryAddressDto;
import com.backend.ecommerce.dto.UserAddressResponse;

import java.util.List;

public interface DeliveryAddressService extends CrudService<DeliveryAddressDto, Integer> {
	List<MyDeliveryAddressDto> findMyAddresses(String email);

	MyDeliveryAddressDto findMyAddressById(String email, Integer id);

	MyDeliveryAddressDto createMyAddress(String email, MyDeliveryAddressDto dto);

	MyDeliveryAddressDto updateMyAddress(String email, Integer id, MyDeliveryAddressDto dto);

	void deleteMyAddress(String email, Integer id);

	List<UserAddressResponse> findMyUserAddresses(String email);
}
