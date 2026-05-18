package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.PromoDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PromoService extends CrudService<PromoDto, Integer> {
    @Transactional(readOnly = true)
    List<PromoDto> findAllActive();
}
