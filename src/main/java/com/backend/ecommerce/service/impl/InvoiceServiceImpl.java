package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.InvoiceDto;
import com.backend.ecommerce.entity.Invoice;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.InvoiceRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> findAll() {
        return invoiceRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public InvoiceDto create(InvoiceDto dto) {
        Invoice entity = new Invoice();
        applyDtoToEntity(dto, entity);
        return toDto(invoiceRepository.save(entity));
    }

    @Override
    public InvoiceDto update(Integer id, InvoiceDto dto) {
        Invoice entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(invoiceRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Invoice entity = getEntityById(id);
        invoiceRepository.delete(entity);
    }

    private Invoice getEntityById(Integer id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
    }

    private void applyDtoToEntity(InvoiceDto dto, Invoice entity) {
        entity.setDateInvoice(dto.getDateInvoice());
        entity.setTotal(dto.getTotal());
        entity.setInvoiceNumber(dto.getInvoiceNumber());
        if (dto.getOrderId() != null) {
            entity.setOrder(orderRepository.findById(dto.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + dto.getOrderId())));
        } else {
            entity.setOrder(null);
        }
    }

    private InvoiceDto toDto(Invoice entity) {
        InvoiceDto dto = new InvoiceDto();
        dto.setId(entity.getId());
        dto.setDateInvoice(entity.getDateInvoice());
        dto.setTotal(entity.getTotal());
        dto.setInvoiceNumber(entity.getInvoiceNumber());
        dto.setOrderId(entity.getOrder() != null ? entity.getOrder().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
