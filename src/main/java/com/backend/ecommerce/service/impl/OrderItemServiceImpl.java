package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.OrderItemDto;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderItemRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final VariantRepository variantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemDto> findAll() {
        return orderItemRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderItemDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public OrderItemDto create(OrderItemDto dto) {
        OrderItem entity = new OrderItem();
        applyDtoToEntity(dto, entity);
        return toDto(orderItemRepository.save(entity));
    }

    @Override
    public OrderItemDto update(Integer id, OrderItemDto dto) {
        OrderItem entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(orderItemRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        OrderItem entity = getEntityById(id);
        orderItemRepository.delete(entity);
    }

    private OrderItem getEntityById(Integer id) {
        return orderItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrderItem not found with id: " + id));
    }

    private void applyDtoToEntity(OrderItemDto dto, OrderItem entity) {
        entity.setQuantity(dto.getQuantity());
        entity.setTotalPrice(dto.getTotalPrice());
        entity.setUnitPrice(dto.getUnitPrice());
        if (dto.getOrderId() != null) {
            entity.setOrder(orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId())));
        } else {
            entity.setOrder(null);
        }
        if (dto.getVariantId() != null) {
            entity.setVariant(variantRepository.findById(dto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + dto.getVariantId())));
        } else {
            entity.setVariant(null);
        }
    }

    private OrderItemDto toDto(OrderItem entity) {
        OrderItemDto dto = new OrderItemDto();
        dto.setId(entity.getId());
        dto.setQuantity(entity.getQuantity());
        dto.setTotalPrice(entity.getTotalPrice());
        dto.setUnitPrice(entity.getUnitPrice());
        dto.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
        dto.setVariantId(entity.getVariant() != null ? entity.getVariant().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
