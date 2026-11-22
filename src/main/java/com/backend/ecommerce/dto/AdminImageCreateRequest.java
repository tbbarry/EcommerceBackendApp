package com.backend.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminImageCreateRequest {
    // Option 1: Upload de fichier
    private MultipartFile file;
    
    // Option 2: URL directe (si pas de fichier)
    private String url;
    
    private String alt;
    private boolean isMain;
    private Integer displayOrder;
    private Integer visualAttributeValueId;
}
