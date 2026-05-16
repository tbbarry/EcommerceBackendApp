package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public OrderDto create(OrderDto dto) {
        Order entity = new Order();
        applyDtoToEntity(dto, entity);
        return toDto(orderRepository.save(entity));
    }

    @Override
    public OrderDto update(Integer id, OrderDto dto) {
        Order entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(orderRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Order entity = getEntityById(id);
        orderRepository.delete(entity);
    }

    private Order getEntityById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void applyDtoToEntity(OrderDto dto, Order entity) {
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setDateOrder(dto.getDateOrder());
        entity.setTaxAmount(dto.getTaxAmount());
        entity.setSubtotal(dto.getSubtotal());
        entity.setShippingOrder(dto.getShippingOrder());
        entity.setTotal(dto.getTotal());
        entity.setStatus(dto.getStatus());
        if (dto.getUserId() != null) {
            entity.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId())));
        } else {
            entity.setUser(null);
        }
        if (dto.getDeliveryAddressId() != null) {
            entity.setDeliveryAddress(deliveryAddressRepository.findById(dto.getDeliveryAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + dto.getDeliveryAddressId())));
        } else {
            entity.setDeliveryAddress(null);
        }
    }

    private OrderDto toDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setDateOrder(entity.getDateOrder());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setSubtotal(entity.getSubtotal());
        dto.setShippingOrder(entity.getShippingOrder());
        dto.setTotal(entity.getTotal());
        dto.setStatus(entity.getStatus());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setDeliveryAddressId(entity.getDeliveryAddress() != null ? entity.getDeliveryAddress().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
