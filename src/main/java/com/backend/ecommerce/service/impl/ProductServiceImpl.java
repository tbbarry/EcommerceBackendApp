package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ProductCreateRequest;
import com.backend.ecommerce.dto.ProductDto;
import com.backend.ecommerce.dto.ProductResponse;
import com.backend.ecommerce.dto.ProductFullResponse;
import com.backend.ecommerce.dto.VariantFullRequest;
import com.backend.ecommerce.dto.VariantAttributeValueRequest;
import com.backend.ecommerce.dto.ProductImageRequest;
import com.backend.ecommerce.dto.VariantFullResponse;
import com.backend.ecommerce.dto.VariantAttributeValueDto;
import com.backend.ecommerce.dto.VariantStockResponse;
import com.backend.ecommerce.dto.ProductImageDto;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.VariantAttributeValue;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.CategoryRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.AttributeValueRepository;
import com.backend.ecommerce.repository.VariantAttributeValueRepository;
import com.backend.ecommerce.service.ProductService;
import com.backend.ecommerce.service.CatalogProductPricingIndexService;
import com.backend.ecommerce.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VariantRepository variantRepository;
    private final StockRepository stockRepository;
    private final ProductImageRepository productImageRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final VariantAttributeValueRepository variantAttributeValueRepository;
    private final CatalogProductPricingIndexService catalogProductPricingIndexService;
    private final ImageUploadService imageUploadService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(this::toProductResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream()
                .map(this::toProductDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse createProduct(ProductCreateRequest request) {
        // Cette méthode semble mal nommée (devrait être dans un service de création)
        // Mais on la garde pour la compatibilité
        return null;
    }

    @Override
    @Transactional
    public ProductFullResponse createProductFull(ProductCreateRequest request) {
        // Validation : la catégorie doit exister
        var category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.getCategoryId()));

        // Créer le produit de base
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setCategory(category);
        product.setSlug(ProductIdentifiers.slug(product));

        // Sauvegarder le produit
        Product savedProduct = productRepository.save(product);
        System.out.println("Saved product with ID: " + savedProduct.getId());

        // Créer les variantes avec leurs attributs et stocks
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (VariantFullRequest variantRequest : request.getVariants()) {
                createVariantWithStock(savedProduct, variantRequest);
            }
        }

        // Créer les images du produit
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            for (ProductImageRequest imageRequest : request.getImages()) {
                createProductImage(savedProduct, imageRequest);
            }
        }

        // Indexer apres les variantes, les attributs, les stocks et les images.
        catalogProductPricingIndexService.refreshProduct(savedProduct.getId());
        return toProductFullResponse(savedProduct);
    }

    /**
     * Crée une variante avec ses attributs et son stock.
     * Cette méthode est appelée dans le contexte de la transaction de createProductFull.
     */
    @Transactional
    private void createVariantWithStock(Product product, VariantFullRequest variantRequest) {
        // Créer et sauvegarder la variante
        Variant variant = new Variant();
        variant.setSku(ProductIdentifiers.sku(product));
        variant.setPrice(variantRequest.getPrice());
        variant.setProduct(product);

        Variant savedVariant = variantRepository.save(variant);
        // Important : flush pour que Hibernate génère l'ID immédiatement
        variantRepository.flush();
        System.out.println("Saved variant with ID: " + savedVariant.getId());

        // Créer les associations entre variante et valeurs d'attributs
        if (variantRequest.getAttributeValueIds() != null && !variantRequest.getAttributeValueIds().isEmpty()) {
            for (VariantAttributeValueRequest attrValueRequest : variantRequest.getAttributeValueIds()) {
                // Vérifier que la valeur d'attribut existe
                System.out.println("Looking for attribute value with ID: " + attrValueRequest.getAttributeValueId());
                var attributeValue = attributeValueRepository.findById(attrValueRequest.getAttributeValueId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "AttributeValue not found with id: " + attrValueRequest.getAttributeValueId()));

                // Créer l'association
                VariantAttributeValue variantAttributeValue = new VariantAttributeValue();
                variantAttributeValue.setVariant(savedVariant);
                variantAttributeValue.setAttributeValue(attributeValue);

                variantAttributeValueRepository.save(variantAttributeValue);
            }
        }
        System.out.println("Created associations for variant with ID: " + savedVariant.getId());

        // Créer le stock initial pour la variante
        Stock stock = new Stock();
        stock.setVariant(savedVariant);
        stock.setQuantity(variantRequest.getQuantity());
        stock.setReservedQuantity(0);

        stockRepository.save(stock);
    }

    /**
     * Crée une image de produit avec référence optionnelle à une valeur d'attribut visuel.
     * Cette méthode est appelée dans le contexte de la transaction de createProductFull.
     */
    @Transactional
    private void createProductImage(Product product, ProductImageRequest imageRequest) {
        ProductImage image = new ProductImage();
        image.setUrl(imageUploadService.storeProductImageData(imageRequest.getUrl(), product.getId()));
        image.setMain(imageRequest.isMain());
        image.setDisplayOrder(imageRequest.getDisplayOrder() != null ? imageRequest.getDisplayOrder() : 0);
        image.setProduct(product);

        // Si une valeur d'attribut visuel est spécifiée, la charger et l'associer
        if (imageRequest.getVisualAttributeValueId() != null) {
            var attributeValue = attributeValueRepository.findById(imageRequest.getVisualAttributeValueId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "AttributeValue not found with id: " + imageRequest.getVisualAttributeValueId()));
            image.setVisualAttributeValue(attributeValue);
        }
        image.setAlt(ProductIdentifiers.imageAlt(product, image.getVisualAttributeValue()));

        productImageRepository.save(image);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductFullResponse getProductFull(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        return toProductFullResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toProductDto(product);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        catalogProductPricingIndexService.deleteProduct(id);
        productRepository.delete(product);
    }

    @Override
    @Transactional
    public ProductDto update(Integer id, ProductDto dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            product.setDescription(dto.getDescription());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getBrand() != null) {
            product.setBrand(dto.getBrand());
        }
        if (dto.getSlug() != null) {
            product.setSlug(dto.getSlug());
        }
        
        Product updatedProduct = productRepository.save(product);
        catalogProductPricingIndexService.refreshProduct(updatedProduct.getId());
        return toProductDto(updatedProduct);
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setBrand(dto.getBrand());
        product.setSlug(dto.getSlug() != null ? dto.getSlug() : generateSlug(dto.getName()));
        
        Product savedProduct = productRepository.save(product);
        catalogProductPricingIndexService.refreshProduct(savedProduct.getId());
        return toProductDto(savedProduct);
    }

    private ProductFullResponse toProductFullResponse(Product product) {
        ProductFullResponse dto = new ProductFullResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSlug(product.getSlug());
        dto.setBrand(product.getBrand());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        
        // Mapper les variantes du produit
        if (product.getVariants() != null && !product.getVariants().isEmpty()) {
            product.getVariants().forEach(variant ->
                    dto.getVariants().add(toVariantFullResponse(variant))
            );
        }
        
        // Mapper les images du produit
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            product.getImages().forEach(image ->
                    dto.getImages().add(toProductImageDto(image))
            );
        }
        
        return dto;
    }

    /**
     * Convertit une entité Variant en VariantFullResponse avec ses attributs et son stock.
     */
    private VariantFullResponse toVariantFullResponse(Variant variant) {
        VariantFullResponse dto = new VariantFullResponse();
        dto.setId(variant.getId());
        dto.setSku(variant.getSku());
        dto.setPrice(variant.getPrice());
        dto.setCreatedAt(variant.getCreatedAt());
        dto.setUpdatedAt(variant.getUpdatedAt());
        
        // Mapper les attributs de la variante
        if (variant.getAttributeValues() != null && !variant.getAttributeValues().isEmpty()) {
            variant.getAttributeValues().forEach(vav ->
                    dto.getAttributeValues().add(toVariantAttributeValueDto(vav))
            );
        }
        
        // Mapper le stock de la variante
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

    /**
     * Convertit une entité VariantAttributeValue en VariantAttributeValueDto.
     */
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

    /**
     * Convertit une entité ProductImage en ProductImageDto.
     */
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

    private ProductResponse toProductResponse(Product product) {
        ProductResponse dto = new ProductResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setSlug(product.getSlug());
        dto.setBrand(product.getBrand());
        return dto;
    }

    private ProductDto toProductDto(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setSlug(product.getSlug());
        dto.setBrand(product.getBrand());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }

    private String generateSlug(String name) {
        return name
                .toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9-]", "")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
}
