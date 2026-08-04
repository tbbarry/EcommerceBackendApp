package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.dto.OrderItemDto;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final TaxServiceImpl taxServiceImpl;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final VariantRepository variantRepository;

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
        entity.setShippingOrder(dto.getShippingOrder());
        entity.setStatus(dto.getStatus());

        if (dto.getDeliveryAddressId() == null) {
            throw new BusinessException("Une adresse de livraison est obligatoire pour valider la commande");
        }

        if (dto.getUserId() != null) {
            entity.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId())));
        } else {
            entity.setUser(null);
        }

        if (dto.getDeliveryAddressId() != null) {
            entity.setDeliveryAddress(deliveryAddressRepository.findByIdAndDeletedFalse(dto.getDeliveryAddressId())
                    .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + dto.getDeliveryAddressId())));
        } else {
            entity.setDeliveryAddress(null);
        }

        if (entity.getTaxRate() == null) {
            entity.setTaxRate(taxServiceImpl.getCurrentTaxRate());
        }

        calculateOrderItemsAndSubtotal(dto, entity);
        calculateOrderAmounts(entity);
    }

    private void calculateOrderItemsAndSubtotal(OrderDto dto, Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;

        order.getOrderItems().clear();

        for (OrderItemDto itemDto : dto.getItems()) {
            Variant variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + itemDto.getVariantId()));

            BigDecimal unitPrice = variant.getPrice();

            BigDecimal totalPrice = unitPrice
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(totalPrice);

            order.getOrderItems().add(orderItem);

            subtotal = subtotal.add(totalPrice);
        }

        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
    }

    private OrderDto toDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setDateOrder(entity.getDateOrder());
        dto.setTaxRate(entity.getTaxRate());
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

    @Override
    public BigDecimal getTotalPaidOrdersAmount() {
        return orderRepository.getTotalPaidOrdersAmount();
    }

    private void calculateOrderAmounts(Order order) {
        BigDecimal subtotal = order.getSubtotal();
        BigDecimal shippingOrder = order.getShippingOrder();
        BigDecimal taxRate = order.getTaxRate();

        BigDecimal taxAmount = subtotal
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal
                .add(shippingOrder)
                .add(taxAmount)
                .setScale(2, RoundingMode.HALF_UP);

        order.setTaxAmount(taxAmount);
        order.setTotal(total);
    }
}
