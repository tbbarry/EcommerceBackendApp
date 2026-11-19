package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FacetResponse {

    private String code;

    private List<String> values;
}