package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageDto {
    private Integer id;
    private String url;
    private String alt;
    private Integer productId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
