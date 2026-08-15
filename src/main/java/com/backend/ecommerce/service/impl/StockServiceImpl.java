package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.StockDto;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.StockReservation;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.enums.StockReservationStatus;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.StockRepository;
import com.backend.ecommerce.repository.StockReservationRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final VariantRepository variantRepository;
    private final StockReservationRepository stockReservationRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StockDto> findAll() {
        return stockRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public StockDto create(StockDto dto) {

        Stock entity = new Stock();

        applyDtoToEntity(dto, entity);

        return toDto(stockRepository.save(entity));
    }

    @Override
    public StockDto update(Integer id, StockDto dto) {

        Stock entity = getEntityById(id);

        applyDtoToEntity(dto, entity);

        return toDto(stockRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {

        Stock entity = getEntityById(id);

        stockRepository.delete(entity);
    }

    /**
     * Réserve une quantité de stock pour une variante.
     *
     * Le Stock est verrouillé pendant toute la transaction
     * afin d'éviter que deux clients réservent simultanément
     * une quantité qui n'existe pas.
     */

    @Override
    @Transactional
    public void reserveStock(Integer variantId, Integer quantity, Integer orderId) {

        // ============================================================
        // 1. VALIDATION DE LA QUANTITÉ DEMANDÉE
        // ============================================================

        if (quantity == null || quantity <= 0) {
            throw new BusinessException(
                    "Quantity must be greater than zero", "QUANTITY_MUST_BE_GREATER_THAN_ZERO"
            );
        }


        // ============================================================
        // 2. RÉCUPÉRATION DU STOCK AVEC VERROU PESSIMISTE
        // ============================================================
        //
        // findByVariantId() utilise :
        //
        // @Lock(LockModeType.PESSIMISTIC_WRITE)
        //
        // Le stock est donc verrouillé pendant cette transaction.
        //
        // Si un deuxième client essaie de réserver le même variant
        // simultanément, sa requête attendra que cette transaction
        // soit terminée.
        //

        Stock stock = stockRepository.findByVariantId(variantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock not found for variant: " + variantId, "STOCK_NOT_FOUND_FOR_VARIANT"
                        )
                );


        // ============================================================
        // 3. CALCUL DE LA QUANTITÉ DISPONIBLE
        // ============================================================
        //
        // availableQuantity =
        //      quantity - reservedQuantity
        //
        // Exemple :
        //
        // quantity         = 10
        // reservedQuantity = 6
        // availableQuantity = 4
        //

        int availableQuantity = stock.getAvailableQuantity();


        // ============================================================
        // 4. VÉRIFICATION DU STOCK
        // ============================================================
        //
        // Cette vérification est faite APRÈS avoir obtenu le verrou.
        //
        // C'est important pour éviter que deux clients réservent
        // simultanément une quantité supérieure au stock disponible.
        //

        if (availableQuantity < quantity) {
            throw new BusinessException(
                    "Insufficient stock. Available: "
                            + availableQuantity
                            + ", requested: "
                            + quantity, "INSUFFICIENT_STOCK"
            );
        }


        // ============================================================
        // 5. RÉCUPÉRATION DE LA COMMANDE
        // ============================================================

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + orderId, "ORDER_NOT_FOUND"
                        )
                );


        // ============================================================
        // 6. AUGMENTATION DU STOCK RÉSERVÉ
        // ============================================================
        //
        // IMPORTANT :
        //
        // On NE DIMINUE PAS quantity ici.
        //
        // quantity représente le stock physique total.
        //
        // On augmente uniquement reservedQuantity.
        //
        // Le stock réellement disponible devient donc :
        //
        // quantity - reservedQuantity
        //
        // La diminution définitive de quantity sera effectuée
        // après confirmation du paiement dans le webhook.
        //

        stock.setReservedQuantity(
                stock.getReservedQuantity() + quantity
        );


        // ============================================================
        // 7. CRÉATION DE LA RÉSERVATION
        // ============================================================
        //
        // Cette entité permet de savoir :
        //
        // - quelle commande a réservé le stock
        // - quel variant est concerné
        // - quelle quantité est réservée
        // - si la réservation est ACTIVE
        // - quand elle expire
        //

        StockReservation reservation =
                StockReservation.builder()
                        .order(order)
                        .variant(stock.getVariant())
                        .quantity(quantity)
                        .status(StockReservationStatus.ACTIVE)
                        .expiresAt(
                                LocalDateTime.now().plusMinutes(15)
                        )
                        .build();

        stockReservationRepository.save(reservation);


        // ============================================================
        // 8. FIN DE LA MÉTHODE
        // ============================================================
        //
        // Comme la méthode est @Transactional :
        //
        // - reservedQuantity sera persisté
        // - StockReservation sera persistée
        // - le verrou PESSIMISTIC_WRITE sera libéré au COMMIT
        //
        // Si une exception RuntimeException survient avant le COMMIT,
        // la transaction est rollbackée.
    }


    private Stock getEntityById(Integer id) {

        return stockRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Stock not found with id: " + id, "STOCK_NOT_FOUND"
                        )
                );
    }

    private void applyDtoToEntity(StockDto dto, Stock entity) {

        if (dto.getQuantity() != null) {
            entity.setQuantity(
                    Math.max(0, dto.getQuantity())
            );
        }

        if (dto.getVariantId() != null) {

            Variant variant = variantRepository.findById(dto.getVariantId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Variant not found with id: " + dto.getVariantId(), "VARIANT_NOT_FOUND"
                            )
                    );

            entity.setVariant(variant);

        } else {

            entity.setVariant(null);
        }
    }

    private StockDto toDto(Stock entity) {

        StockDto dto = new StockDto();

        dto.setId(entity.getId());

        // Stock.quantity est maintenant la source de vérité.
        dto.setQuantity(entity.getQuantity());

        dto.setVariantId(
                entity.getVariant() != null
                        ? entity.getVariant().getId()
                        : null
        );

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }
    @Override
    @Transactional
    public void confirmReservations(Integer orderId) {

        List<StockReservation> reservations =
                stockReservationRepository.findByOrderIdAndStatus(
                        orderId,
                        StockReservationStatus.ACTIVE
                );

        if (reservations.isEmpty()) {
            return;
        }

        for (StockReservation reservation : reservations) {

            Stock stock = stockRepository
                    .findByVariantId(reservation.getVariant().getId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Stock not found for variant: "
                                            + reservation.getVariant().getId(),
                                    "STOCK_NOT_FOUND_FOR_VARIANT"
                            )
                    );

            int quantity = reservation.getQuantity();
            if (stock.getQuantity() < quantity) {
                throw new BusinessException(
                        "Insufficient physical stock while confirming reservation",
                        "INSUFFICIENT_PHYSICAL_STOCK"
                );
            }

            if (stock.getReservedQuantity() < quantity) {
                throw new BusinessException(
                        "Invalid reserved stock quantity",
                        "INVALID_RESERVED_STOCK_QUANTITY"
                );
            }

            // Le stock physique devient réellement vendu
            stock.setQuantity(
                    stock.getQuantity() - quantity
            );

            // La quantité n'est plus réservée
            stock.setReservedQuantity(
                    stock.getReservedQuantity() - quantity
            );

            // La réservation est terminée
            reservation.setStatus(
                    StockReservationStatus.CONFIRMED
            );

            stockRepository.save(stock);
            stockReservationRepository.save(reservation);
        }
    }
}