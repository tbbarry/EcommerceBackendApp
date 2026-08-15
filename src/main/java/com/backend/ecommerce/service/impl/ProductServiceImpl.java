package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ProductCreateRequest;
import com.backend.ecommerce.dto.ProductDto;
import com.backend.ecommerce.dto.ProductImageResponse;
import com.backend.ecommerce.dto.ProductResponse;
import com.backend.ecommerce.dto.VariantRequest;
import com.backend.ecommerce.dto.VariantResponse;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductColor;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.ProductColorRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final VariantRepository variantRepository;
    private final ProductColorRepository productColorRepository;
    private final ProductImageRepository productImageRepository;
    private final StockRepository stockRepository;
    private final SkuGeneratorService skuGeneratorService;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public ProductDto create(ProductDto dto) {
        ProductCreateRequest request = new ProductCreateRequest();
        request.setName(dto.getName());
        request.setDescription(dto.getDescription());
        request.setPrice(dto.getPrice());
        request.setBrand(dto.getBrand());
        request.setStock(0);

        ProductResponse created = createProduct(request);
        return toDto(getEntityById(created.getId()));
    }

    @Override
    public ProductDto update(Integer id, ProductDto dto) {
        Product entity = getEntityById(id);
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setBrand(dto.getBrand());
        entity.setSlug(generateSlug(dto.getBrand(), dto.getName()));
        return toDto(productRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Product entity = getEntityById(id);
        productRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> findAllPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findAll(pageable).map(this::toProductResponseLite);
    }

    @Override
    public ProductResponse createProduct(ProductCreateRequest request) {
        validateCreateRequest(request);

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription() == null ? "" : request.getDescription());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setSlug(generateSlug(request.getBrand(), request.getName()));

        Product savedProduct = productRepository.save(product);

        Map<String, ProductColor> colorMap = saveProductColors(savedProduct, request.getColors());
        List<Variant> variants = buildAndSaveVariants(savedProduct, request, colorMap);
        saveProductImages(savedProduct, request.getImageUrls(), colorMap);

        if (variants.isEmpty()) {
            throw new BusinessException("Chaque produit doit avoir au moins une variante", "PRODUCT_MUST_HAVE_AT_LEAST_ONE_VARIANT");
        }

        return buildProductResponse(savedProduct, colorMap, variants);
    }

    private Product getEntityById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void validateCreateRequest(ProductCreateRequest request) {
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException("Le prix doit etre superieur ou egal a 0", "PRICE_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO");
        }

        if (request.getStock() != null && request.getStock() < 0) {
            throw new BusinessException("Le stock doit etre superieur ou egal a 0", "STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO");
        }
    }

    private Map<String, ProductColor> saveProductColors(Product product, List<String> colors) {
        Map<String, ProductColor> colorMap = new HashMap<>();
        if (colors == null) {
            return colorMap;
        }

        for (String colorName : colors) {
            String name = sanitize(colorName);
            if (name == null) {
                continue;
            }

            String key = name.toLowerCase(Locale.ROOT);
            if (colorMap.containsKey(key)) {
                continue;
            }

            ProductColor productColor = new ProductColor();
            productColor.setName(name);
            productColor.setProduct(product);
            colorMap.put(key, productColorRepository.save(productColor));
        }
        return colorMap;
    }

    private List<Variant> buildAndSaveVariants(Product product, ProductCreateRequest request, Map<String, ProductColor> colorMap) {
        List<Variant> createdVariants = new ArrayList<>();
        Set<String> comboGuard = new HashSet<>();

        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            for (VariantRequest variantRequest : request.getVariants()) {
                createdVariants.add(createVariant(product, variantRequest.getColor(), variantRequest.getSize(), variantRequest.getStock(), comboGuard));
            }
            return createdVariants;
        }

        List<String> colors = request.getColors() == null ? List.of() : request.getColors().stream()
            .map(this::sanitize)
                .filter(value -> value != null)
                .toList();

        List<String> sizes = request.getSizes() == null ? List.of() : request.getSizes().stream()
                .map(this::sanitize)
                .filter(value -> value != null)
                .toList();

        int stock = request.getStock() == null ? 0 : request.getStock();

        if (!colors.isEmpty() && !sizes.isEmpty()) {
            for (String color : colors) {
                for (String size : sizes) {
                    createdVariants.add(createVariant(product, color, size, stock, comboGuard));
                }
            }
            return createdVariants;
        }

        if (!colors.isEmpty()) {
            for (String color : colors) {
                createdVariants.add(createVariant(product, color, null, stock, comboGuard));
            }
            return createdVariants;
        }

        if (!sizes.isEmpty()) {
            for (String size : sizes) {
                createdVariants.add(createVariant(product, null, size, stock, comboGuard));
            }
            return createdVariants;
        }

        createdVariants.add(createVariant(product, null, null, stock, comboGuard));
        return createdVariants;
    }

    private Variant createVariant(
        Product product,
        String color,
        String size,
        Integer stock,
        Set<String> comboGuard
    ) {
        String normalizedColor = sanitize(color);
        String normalizedSize = sanitize(size);
        Integer safeStock = stock == null ? 0 : stock;

        // Vérification du stock
        if (safeStock < 0) {
            throw new BusinessException(
                    "Le stock d'une variante doit etre superieur ou egal a 0",
                    "VARIANT_STOCK_MUST_BE_GREATER_THAN_OR_EQUAL_TO_ZERO"
            );
        }

        /*
        * Protection contre les doublons dans la requête actuelle.
        *
        * Exemples :
        * null + null -> "<null>|<null>"
        * Rouge + null -> "rouge|<null>"
        * null + M -> "<null>|m"
        * Rouge + M -> "rouge|m"
        */
        String comboKey =
                (normalizedColor == null
                        ? "<null>"
                        : normalizedColor.toLowerCase(Locale.ROOT))
                + "|"
                + (normalizedSize == null
                        ? "<null>"
                        : normalizedSize.toLowerCase(Locale.ROOT));

        if (!comboGuard.add(comboKey)) {
            throw new BusinessException(
                    "Deux variantes identiques ne sont pas autorisees",
                    "DUPLICATE_VARIANT_NOT_ALLOWED"
            );
        }

        /*
        * Vérification des doublons déjà présents en base.
        */
        boolean alreadyExists;

        if (normalizedColor == null && normalizedSize == null) {

            /*
            * Produit unique :
            * aucune couleur + aucune taille
            */
            alreadyExists =
                    variantRepository.existsByProductIdAndProductColorIsNullAndSizeIsNull(
                            product.getId()
                    );

        } else if (normalizedColor != null && normalizedSize != null) {

            /*
            * Variante avec couleur + taille
            */
            alreadyExists =
                    variantRepository
                            .existsByProductIdAndProductColorNameIgnoreCaseAndSizeIgnoreCase(
                                    product.getId(),
                                    normalizedColor,
                                    normalizedSize
                            );

        } else if (normalizedColor != null) {

            /*
            * Variante avec couleur uniquement
            */
            alreadyExists =
                    variantRepository
                            .existsByProductIdAndProductColorNameIgnoreCaseAndSizeIsNull(
                                    product.getId(),
                                    normalizedColor
                            );

        } else {

            /*
            * Variante avec taille uniquement
            */
            alreadyExists =
                    variantRepository
                            .existsByProductIdAndProductColorIsNullAndSizeIgnoreCase(
                                    product.getId(),
                                    normalizedSize
                            );
        }

        if (alreadyExists) {
            throw new BusinessException(
                    "Une variante identique existe deja",
                    "IDENTICAL_VARIANT_ALREADY_EXISTS"
            );
        }

        /*
        * Création de la variante
        */
        Variant variant = new Variant();

        variant.setProduct(product);
        variant.setSize(normalizedSize);
        variant.setPrice(product.getPrice());

        /*
        * Association de la couleur.
        *
        * Si aucune couleur n'est fournie,
        * productColor reste null.
        */
        if (normalizedColor != null) {

            ProductColor productColor =
                    productColorRepository
                            .findByProductIdAndNameIgnoreCase(
                                    product.getId(),
                                    normalizedColor
                            )
                            .orElseGet(() -> {

                                ProductColor newColor = new ProductColor();

                                newColor.setProduct(product);
                                newColor.setName(normalizedColor);

                                return productColorRepository.save(newColor);
                            });

            variant.setProductColor(productColor);
        }

        /*
        * Génération du SKU.
        *
        * Produit sans couleur ni taille :
        * SKU simple.
        *
        * Produit avec au moins une variante :
        * SKU basé sur couleur/taille.
        */
        if (normalizedColor == null && normalizedSize == null) {

            variant.setSku(
                    skuGeneratorService.generateSimpleSku(
                            product.getName()
                    )
            );

        } else {

            variant.setSku(
                    skuGeneratorService.generateVariantSku(
                            product.getName(),
                            normalizedColor,
                            normalizedSize
                    )
            );
        }

        /*
        * Sauvegarde de la variante
        */
        Variant savedVariant = variantRepository.save(variant);

        /*
        * Création du stock associé à la variante
        */
        Stock stockRef = new Stock();

        stockRef.setVariant(savedVariant);
        stockRef.setQuantity(safeStock);

        stockRepository.save(stockRef);

        return savedVariant;
    }


    private void saveProductImages(Product product, List<String> imageUrls, Map<String, ProductColor> colorMap) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        int index = 0;
        for (String url : imageUrls) {
            String cleanedUrl = sanitize(url);
            if (cleanedUrl == null) {
                continue;
            }

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setProductColor(null);
            image.setUrl(cleanedUrl);
            image.setAlt(product.getName() + " image " + (index + 1));
            image.setMain(index == 0);
            productImageRepository.save(image);
            index++;
        }
    }

    private ProductResponse buildProductResponse(Product product, Map<String, ProductColor> colorMap, List<Variant> variants) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setBrand(product.getBrand());
        response.setSlug(product.getSlug());

        response.setColors(colorMap.values().stream().map(ProductColor::getName).toList());
        response.setVariants(variants.stream().map(variant ->
                new VariantResponse(
                        variant.getId(),
                        variant.getSku(),
                        variant.getStock().getAvailableQuantity(),
                        variant.getSize(),
                        variant.getProductColor() != null ? variant.getProductColor().getName() : null
                )
        ).toList());

        response.setImages(productImageRepository.findByProductId(product.getId()).stream()
                .map(image -> new ProductImageResponse(
                        image.getId(),
                        image.getUrl(),
                        image.getAlt(),
                        image.isMain(),
                        image.getProductColor() != null ? image.getProductColor().getName() : null
                ))
                .toList());

        return response;
    }

    private ProductDto toDto(Product entity) {
        ProductDto dto = new ProductDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setPrice(entity.getPrice());
        dto.setSlug(entity.getSlug());
        dto.setBrand(entity.getBrand());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

        private ProductResponse toProductResponseLite(Product entity) {
        ProductResponse response = new ProductResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setPrice(entity.getPrice());
        response.setBrand(entity.getBrand());
        response.setSlug(entity.getSlug());

        response.setColors(productColorRepository.findByProductId(entity.getId()).stream()
            .map(ProductColor::getName)
            .toList());

        response.setVariants(variantRepository.findByProductId(entity.getId()).stream()
            .map(variant -> new VariantResponse(
                variant.getId(),
                variant.getSku(),
                variant.getStock().getAvailableQuantity(),
                variant.getSize(),
                variant.getProductColor() != null ? variant.getProductColor().getName() : null
            ))
            .toList());

        response.setImages(productImageRepository.findByProductId(entity.getId()).stream()
            .map(image -> new ProductImageResponse(
                image.getId(),
                image.getUrl(),
                image.getAlt(),
                image.isMain(),
                image.getProductColor() != null ? image.getProductColor().getName() : null
            ))
            .toList());

        return response;
        }

    private String generateSlug(String brand, String name) {
        String value = (brand == null ? "" : brand) + "_" + (name == null ? "" : name);

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_+|_+$", "");
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.isEmpty() ? null : cleaned;
    }
}
