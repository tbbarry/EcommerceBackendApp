package com.backend.ecommerce.service.impl;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import java.util.List;
import com.backend.ecommerce.dto.VariantDto;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductColor;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.VariantService;
import com.backend.ecommerce.repository.ProductColorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
@Transactional
public class VariantServiceImpl implements VariantService {

    private final VariantRepository variantRepository;
    private final ProductRepository productRepository;
    private final ProductColorRepository productColorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VariantDto> findAll() {
        return variantRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public VariantDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public VariantDto create(VariantDto dto) {
        Variant entity = new Variant();

        applyDtoToEntity(dto, entity);

        return toDto(variantRepository.save(entity));
    }

    @Override
    public VariantDto update(Integer id, VariantDto dto) {
        Variant entity = getEntityById(id);

        applyDtoToEntity(dto, entity);

        return toDto(variantRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Variant entity = getEntityById(id);

        variantRepository.delete(entity);
    }

    private Variant getEntityById(Integer id) {
        return variantRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Variant not found with id: " + id
                        )
                );
    }

    private void applyDtoToEntity(VariantDto dto, Variant entity) {

        entity.setSize(dto.getSize());
        entity.setSku(dto.getSku());
        entity.setPrice(dto.getPrice());

        // Product obligatoire
        if (dto.getProductId() == null) {
            throw new IllegalArgumentException(
                    "productId is required for a variant"
            );
        }

        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with id: "
                                        + dto.getProductId()
                        )
                );

        entity.setProduct(product);

        // ProductColor optionnelle
        if (dto.getProductColorId() != null) {

            ProductColor productColor =
                    productColorRepository.findById(dto.getProductColorId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Product color not found with id: "
                                                    + dto.getProductColorId()
                                    )
                            );

            // Vérification importante :
            // la couleur doit appartenir au produit
            if (!productColor.getProduct().getId()
                    .equals(product.getId())) {

                throw new IllegalArgumentException(
                        "The selected color does not belong to the product"
                );
            }

            entity.setProductColor(productColor);

        } else {
            // Produit sans couleur
            entity.setProductColor(null);
        }
    }

    private VariantDto toDto(Variant entity) {

        VariantDto dto = new VariantDto();

        dto.setId(entity.getId());
        dto.setSize(entity.getSize());
        dto.setSku(entity.getSku());
        dto.setPrice(entity.getPrice());

        dto.setProductId(
                entity.getProduct() != null
                        ? entity.getProduct().getId()
                        : null
        );

        dto.setProductColorId(
                entity.getProductColor() != null
                        ? entity.getProductColor().getId()
                        : null
        );

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
}