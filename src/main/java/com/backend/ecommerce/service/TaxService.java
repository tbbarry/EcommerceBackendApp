package com.backend.ecommerce.service;

import com.backend.ecommerce.dto.TaxSettingDto;

import java.math.BigDecimal;

public interface TaxService  extends CrudService<TaxSettingDto, Integer> {
    BigDecimal getCurrentTaxRate();

    TaxSettingDto getCurrentTaxSetting();

    TaxSettingDto updateCurrentTaxRate(TaxSettingDto dto);
}
