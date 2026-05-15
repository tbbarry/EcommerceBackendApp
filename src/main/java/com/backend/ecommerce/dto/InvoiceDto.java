package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDto {
    private Integer id;
    private Integer orderId;
    private LocalDateTime dateInvoice;
    private BigDecimal total;
    private String invoiceNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
