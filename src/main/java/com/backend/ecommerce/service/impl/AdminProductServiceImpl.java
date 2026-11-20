package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.*;
import com.backend.ecommerce.entity.*;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.*;
import com.backend.ecommerce.service.AdminProductService;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation du service admin de gestion des produits.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final ProductImageRepository productImageRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final CategoryRepository categoryRepository;
    private final CatalogProductPricingIndexService catalogProductPricingIndexService;

    @Override
    public Page<ProductAdminListResponse> getAllProductsForAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        
        return productRepository.findAll(pageable).map(product -> {
            ProductAdminListResponse response = new ProductAdminListResponse();
            response.setId(product.getId());
            response.setName(product.getName());
            response.setBrand(product.getBrand());
            response.setPrice(product.getPrice());
            
            if (product.getCategory() != null) {
                response.setCategoryName(product.getCategory().getName());
            }
            
            response.setTotalVariants(product.getVariants() != null ? product.getVariants().size() : 0);
            
            int totalStock = 0;
            if (product.getVariants() != null) {
                for (Variant variant : product.getVariants()) {
                    if (variant.getStock() != null) {
                        totalStock += variant.getStock().getAvailableQuantity();
                    }
                }
            }
            response.setTotalStock(totalStock);
            
            response.setStatus("ACTIVE");
            response.setCreatedAt(product.getCreatedAt());
            response.setUpdatedAt(product.getUpdatedAt());
            
            return response;
        });
    }

    @Override
    public ProductAdminDetailResponse getProductDetailForAdmin(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
        
        return mapToProductAdminDetailResponse(product);
    }

    // ===== ENDPOINT 1: PATCH /products/{id} =====
    @Override
    @Transactional
    public ProductAdminDetailResponse updateProduct(Integer productId, AdminProductUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (request.getName() != null && !request.getName().isBlank()) {
            product.setName(request.getName());
        }
        
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        
        if (request.getBrand() != null && !request.getBrand().isBlank()) {
            product.setBrand(request.getBrand());
        }
        
        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
            product.setPrice(request.getPrice());
        }
        
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            if (!product.getSlug().equals(request.getSlug())) {
                if (productRepository.existsBySlug(request.getSlug())) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Slug already exists");
                }
            }
            product.setSlug(request.getSlug());
        }
        
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found"));
            product.setCategory(category);
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        Product updated = productRepository.save(product);
        catalogProductPricingIndexService.refreshProduct(productId);
        
        return mapToProductAdminDetailResponse(updated);
    }

    // ===== ENDPOINT 2: POST /products/{id}/variants =====
    @Override
    @Transactional
    public VariantAdminResponse createVariant(Integer productId, AdminVariantCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (request.getSku() == null || request.getSku().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU is required");
        }
        
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be positive");
        }
        
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be >= 0");
        }
        
        if (request.getAttributeValueIds() == null || request.getAttributeValueIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one attribute value is required");
        }
        
        Variant variant = new Variant();
        variant.setSku(request.getSku());
        variant.setPrice(request.getPrice());
        variant.setProduct(product);
        
        Stock stock = new Stock();
        stock.setQuantity(request.getQuantity());
        stock.setReservedQuantity(0);
        stock.setVariant(variant);
        variant.setStock(stock);
        
        for (Integer attributeValueId : request.getAttributeValueIds()) {
            AttributeValue attributeValue = attributeValueRepository.findById(attributeValueId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            "AttributeValue not found with id: " + attributeValueId));
            
            VariantAttributeValue vav = new VariantAttributeValue();
            vav.setVariant(variant);
            vav.setAttributeValue(attributeValue);
            variant.getAttributeValues().add(vav);
        }
        
        product.getVariants().add(variant);
        Product saved = productRepository.save(product);
        
        // Find the saved variant by SKU
        Variant savedVariant = saved.getVariants().stream()
                .filter(v -> v.getSku().equals(request.getSku()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Variant not found after save"));
        
        catalogProductPricingIndexService.refreshProduct(productId);
        
        return toVariantAdminResponse(savedVariant);
    }

    // ===== ENDPOINT 3: PATCH /products/{id}/variants/{variantId} =====
    @Override
    @Transactional
    public VariantAdminResponse updateVariant(Integer productId, Integer variantId, AdminVariantUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        Variant variant = product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));
        
        if (request.getSku() != null && !request.getSku().isBlank()) {
            if (!variant.getSku().equals(request.getSku())) {
                // Vérifier unicité du SKU
                boolean skuExists = productRepository.findAll().stream()
                        .flatMap(p -> p.getVariants().stream())
                        .anyMatch(v -> v.getSku().equals(request.getSku()) && !v.getId().equals(variantId));
                if (skuExists) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "SKU already exists");
                }
            }
            variant.setSku(request.getSku());
        }
        
        if (request.getPrice() != null && request.getPrice().compareTo(BigDecimal.ZERO) >= 0) {
            variant.setPrice(request.getPrice());
        }
        
        if (request.getAttributeValueIds() != null && !request.getAttributeValueIds().isEmpty()) {
            variant.getAttributeValues().clear();
            for (Integer attributeValueId : request.getAttributeValueIds()) {
                AttributeValue attributeValue = attributeValueRepository.findById(attributeValueId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                                "AttributeValue not found with id: " + attributeValueId));
                
                VariantAttributeValue vav = new VariantAttributeValue();
                vav.setVariant(variant);
                vav.setAttributeValue(attributeValue);
                variant.getAttributeValues().add(vav);
            }
        }
        
        variant.setUpdatedAt(LocalDateTime.now());
        Variant updated = productRepository.save(product).getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow();
        
        catalogProductPricingIndexService.refreshProduct(productId);
        return toVariantAdminResponse(updated);
    }

    // ===== ENDPOINT 4: PATCH /products/{id}/variants/{variantId}/stock =====
    @Override
    @Transactional
    public void updateVariantStock(Integer productId, Integer variantId, AdminStockUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        Variant variant = product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));
        
        if (request.getQuantity() == null || request.getQuantity() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be >= 0");
        }
        
        Stock stock = variant.getStock();
        if (stock == null) {
            throw new ResourceNotFoundException("Stock not found for variant");
        }
        
        if (request.getQuantity() < stock.getReservedQuantity()) {
            String message = String.format(
                    "Le stock total ne peut pas être inférieur aux réservations (réservé: %d, nouveau: %d)",
                    stock.getReservedQuantity(), request.getQuantity()
            );
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
        
        stock.setQuantity(request.getQuantity());
        stock.setUpdatedAt(LocalDateTime.now());
        stockRepository.save(stock);
        
        catalogProductPricingIndexService.refreshProduct(productId);
    }

    // ===== ENDPOINT 5: DELETE /products/{id}/variants/{variantId} =====
    @Override
    @Transactional
    public void deleteVariant(Integer productId, Integer variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        Variant variant = product.getVariants().stream()
                .filter(v -> v.getId().equals(variantId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));
        
        if (variant.getStock() != null && variant.getStock().getReservedQuantity() > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Cette variante possède des unités réservées et ne peut pas être supprimée.");
        }
        
        product.getVariants().remove(variant);
        productRepository.save(product);
        
        catalogProductPricingIndexService.refreshProduct(productId);
    }

    // ===== ENDPOINT 6: POST /products/{id}/variants/bulk-delete =====
    @Override
    @Transactional
    public void bulkDeleteVariants(Integer productId, AdminVariantBulkDeleteRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (request.getVariantIds() == null || request.getVariantIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No variant IDs provided");
        }
        
        // Vérifier que TOUTES les variantes appartiennent au produit et n'ont pas de réservations
        for (Integer variantId : request.getVariantIds()) {
            Variant variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, 
                            "Variant " + variantId + " not found in product"));
            
            if (variant.getStock() != null && variant.getStock().getReservedQuantity() > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, 
                        "La variante " + variantId + " possède des unités réservées. Aucune suppression n'a été effectuée.");
            }
        }
        
        // Si tous les contrôles passent, supprimer
        for (Integer variantId : request.getVariantIds()) {
            Variant variant = product.getVariants().stream()
                    .filter(v -> v.getId().equals(variantId))
                    .findFirst()
                    .orElseThrow();
            product.getVariants().remove(variant);
        }
        
        productRepository.save(product);
        catalogProductPricingIndexService.refreshProduct(productId);
    }

    // ===== ENDPOINT 7: POST /products/{id}/images =====
    @Override
    @Transactional
    public ProductImageDto createImage(Integer productId, AdminImageCreateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (request.getUrl() == null || request.getUrl().isBlank() 
                || (!request.getUrl().startsWith("http://") && !request.getUrl().startsWith("https://"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "URL must be valid HTTP/HTTPS");
        }
        
        if (request.getAlt() == null || request.getAlt().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Alt text is required");
        }
        
        ProductImage image = new ProductImage();
        image.setUrl(request.getUrl());
        image.setAlt(request.getAlt());
        image.setMain(request.isMain());
        image.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        image.setProduct(product);
        
        if (request.getVisualAttributeValueId() != null) {
            AttributeValue attributeValue = attributeValueRepository.findById(request.getVisualAttributeValueId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                            "AttributeValue not found with id: " + request.getVisualAttributeValueId()));
            image.setVisualAttributeValue(attributeValue);
        }
        
        // Si cette image est principale, enlever le statut principal des autres
        if (request.isMain()) {
            for (ProductImage existingImage : product.getImages()) {
                if (existingImage.isMain()) {
                    existingImage.setMain(false);
                }
            }
        }
        
        product.getImages().add(image);
        Product saved = productRepository.save(product);
        
        ProductImage savedImage = saved.getImages().stream()
                .filter(img -> img.getUrl().equals(request.getUrl()))
                .findFirst()
                .orElseThrow();
        
        catalogProductPricingIndexService.refreshProduct(productId);
        return toProductImageDto(savedImage);
    }

    // ===== ENDPOINT 8: PATCH /products/{id}/images/{imageId} =====
    @Override
    @Transactional
    public ProductImageDto updateImage(Integer productId, Integer imageId, AdminImageUpdateRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));
        
        if (request.getAlt() != null && !request.getAlt().isBlank()) {
            image.setAlt(request.getAlt());
        }
        
        if (request.getDisplayOrder() != null) {
            image.setDisplayOrder(request.getDisplayOrder());
        }
        
        if (request.getIsMain() != null) {
            if (request.getIsMain()) {
                // Enlever le statut principal des autres images
                for (ProductImage existingImage : product.getImages()) {
                    if (!existingImage.getId().equals(imageId) && existingImage.isMain()) {
                        existingImage.setMain(false);
                    }
                }
                image.setMain(true);
            } else {
                image.setMain(false);
            }
        }
        
        if (request.getVisualAttributeValueId() != null) {
            if (request.getVisualAttributeValueId() == 0 || request.getVisualAttributeValueId() == -1) {
                image.setVisualAttributeValue(null);
            } else {
                AttributeValue attributeValue = attributeValueRepository.findById(request.getVisualAttributeValueId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                                "AttributeValue not found with id: " + request.getVisualAttributeValueId()));
                image.setVisualAttributeValue(attributeValue);
            }
        }
        
        image.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        
        ProductImage updatedImage = saved.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow();
        
        catalogProductPricingIndexService.refreshProduct(productId);
        return toProductImageDto(updatedImage);
    }

    // ===== ENDPOINT 9: DELETE /products/{id}/images/{imageId} =====
    @Override
    @Transactional
    public void deleteImage(Integer productId, Integer imageId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        ProductImage image = product.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));
        
        if (image.isMain() && product.getImages().size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "L'image principale ne peut pas être supprimée tant qu'une autre image n'a pas été définie comme principale.");
        }
        
        product.getImages().remove(image);
        productRepository.save(product);
        
        catalogProductPricingIndexService.refreshProduct(productId);
    }

    // ===== ENDPOINT 10: PATCH /products/{id}/status =====
    @Override
    @Transactional
    public ProductAdminDetailResponse updateProductStatus(Integer productId, AdminProductStatusRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        
        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }
        
        // Vérifier que le statut est valide (ACTIVE, ARCHIVED, etc.)
        try {
            String.valueOf(request.getStatus());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status");
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        Product updated = productRepository.save(product);
        
        catalogProductPricingIndexService.refreshProduct(productId);
        return mapToProductAdminDetailResponse(updated);
    }

    // ===== HELPER METHODS =====

    private ProductAdminDetailResponse mapToProductAdminDetailResponse(Product product) {
        ProductAdminDetailResponse response = new ProductAdminDetailResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setBrand(product.getBrand());
        response.setSlug(product.getSlug());
        response.setPrice(product.getPrice());
        response.setStatus("ACTIVE");
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        if (product.getCategory() != null) {
            response.setCategoryId(product.getCategory().getId());
            response.setCategoryName(product.getCategory().getName());
        }
        
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            for (Variant variant : product.getVariants()) {
                response.getVariants().add(toVariantAdminResponse(variant));
            }
        }
        
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            for (ProductImage image : product.getImages()) {
                response.getImages().add(toProductImageDto(image));
            }
        }
        
        return response;
    }

    private VariantAdminResponse toVariantAdminResponse(Variant variant) {
        VariantAdminResponse dto = new VariantAdminResponse();
        dto.setId(variant.getId());
        dto.setSku(variant.getSku());
        dto.setPrice(variant.getPrice());
        dto.setCreatedAt(variant.getCreatedAt());
        dto.setUpdatedAt(variant.getUpdatedAt());
        
        if (variant.getAttributeValues() != null && !variant.getAttributeValues().isEmpty()) {
            for (VariantAttributeValue vav : variant.getAttributeValues()) {
                dto.getAttributeValues().add(toVariantAttributeValueDto(vav));
            }
        }
        
        if (variant.getStock() != null) {
            Stock stock = variant.getStock();
            VariantStockResponse stockDto = new VariantStockResponse();
            stockDto.setId(stock.getId());
            stockDto.setQuantity(stock.getQuantity());
            stockDto.setReservedQuantity(stock.getReservedQuantity());
            stockDto.setAvailableQuantity(stock.getAvailableQuantity());
            stockDto.setCreatedAt(stock.getCreatedAt());
            stockDto.setUpdatedAt(stock.getUpdatedAt());
            dto.setStock(stockDto);
        }
        
        return dto;
    }

    private VariantAttributeValueDto toVariantAttributeValueDto(VariantAttributeValue vav) {
        VariantAttributeValueDto dto = new VariantAttributeValueDto();
        dto.setId(vav.getId());
        
        if (vav.getAttributeValue() != null) {
            dto.setAttributeValueId(vav.getAttributeValue().getId());
            dto.setAttributeValueName(vav.getAttributeValue().getValue());
            
            if (vav.getAttributeValue().getAttribute() != null) {
                dto.setAttributeId(vav.getAttributeValue().getAttribute().getId());
                dto.setAttributeName(vav.getAttributeValue().getAttribute().getName());
            }
        }
        
        dto.setCreatedAt(vav.getCreatedAt());
        dto.setUpdatedAt(vav.getUpdatedAt());
        
        return dto;
    }

    private ProductImageDto toProductImageDto(ProductImage image) {
        ProductImageDto dto = new ProductImageDto();
        dto.setId(image.getId());
        dto.setUrl(image.getUrl());
        dto.setAlt(image.getAlt());
        dto.setMain(image.isMain());
        dto.setDisplayOrder(image.getDisplayOrder());
        
        if (image.getVisualAttributeValue() != null) {
            dto.setVisualAttributeValueId(image.getVisualAttributeValue().getId());
            dto.setVisualAttributeValueName(image.getVisualAttributeValue().getValue());
        }
        
        dto.setCreatedAt(image.getCreatedAt());
        dto.setUpdatedAt(image.getUpdatedAt());
        
        return dto;
    }
}
