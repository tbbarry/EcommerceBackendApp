package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.PaymentDto;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PaymentDto> findAll() {
        return paymentRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public PaymentDto create(PaymentDto dto) {
        Payment entity = new Payment();
        applyDtoToEntity(dto, entity);
        return toDto(paymentRepository.save(entity));
    }

    @Override
    public PaymentDto update(Integer id, PaymentDto dto) {
        Payment entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(paymentRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Payment entity = getEntityById(id);
        paymentRepository.delete(entity);
    }

    private Payment getEntityById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + id));
    }

    private void applyDtoToEntity(PaymentDto dto, Payment entity) {
        entity.setPaymentMethod(dto.getPaymentMethod());
        entity.setAmount(dto.getAmount());
        entity.setPaidAt(dto.getPaidAt());
        entity.setStatus(dto.getStatus());
        if (dto.getOrderId() != null) {
            entity.setOrder(orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId())));
        } else {
            entity.setOrder(null);
        }
    }

    private PaymentDto toDto(Payment entity) {
        PaymentDto dto = new PaymentDto();
        dto.setId(entity.getId());
        dto.setPaymentMethod(entity.getPaymentMethod());
        dto.setAmount(entity.getAmount());
        dto.setPaidAt(entity.getPaidAt());
        dto.setStatus(entity.getStatus());
        dto.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
