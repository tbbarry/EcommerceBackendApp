package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.CheckoutResponse;
import com.backend.ecommerce.dto.CheckoutSessionData;
import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.dto.OrderItemDto;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.entity.DeliveryPreference;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.IdempotencyKey;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.Payment;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductColor;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.enums.IdempotencyStatus;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.enums.PaymentStatus;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.DeliveryPreferenceRepository;
import com.backend.ecommerce.repository.IdempotencyKeyRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.PaymentRepository;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CouponCalculationService;
import com.backend.ecommerce.service.CouponService;
import com.backend.ecommerce.service.OrderService;
import com.backend.ecommerce.service.ShippingCalculationService;
import com.backend.ecommerce.service.StockService;
import com.backend.ecommerce.service.StripePaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.StripeException;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final TaxServiceImpl taxServiceImpl;
    private final ShippingCalculationService shippingCalculationService;
    private final CouponService couponService;
    private final CouponCalculationService couponCalculationService;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final DeliveryPreferenceRepository deliveryPreferenceRepository;
    private final VariantRepository variantRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    private final StripePaymentService stripePaymentService;
    private final StockService stockService;
    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    // ============================================================
    // CRUD
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAllByUserId(Integer userId) {
        return orderRepository.findAllByUser_Id(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public OrderDto create(OrderDto dto) {
        Order entity = new Order();
        applyDtoToEntity(dto, entity);

        return toDto(orderRepository.save(entity));
    }

    @Override
    public OrderDto update(Integer id, OrderDto dto) {
        Order entity = getEntityById(id);
        applyDtoToEntity(dto, entity);

        return toDto(orderRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Order entity = getEntityById(id);
        orderRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalPaidOrdersAmount() {
        return orderRepository.getTotalPaidOrdersAmount();
    }


    // ============================================================
    // CHECKOUT
    // ============================================================

    
    @Override
    @Transactional
    public CheckoutResponse checkout(Integer userId, CheckoutRequest request, String idempotencyKey) {

        // ============================================================
        // 0. VÉRIFICATION DE L'IDEMPOTENCE
        // ============================================================
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new BusinessException(
                    "Idempotency-Key header is required",
                    "IDEMPOTENCY_KEY_REQUIRED"
            );
        }
        idempotencyKey = idempotencyKey.trim();
        //============================================================
        // 0.1 Vérification d'un requête existante

        Optional<IdempotencyKey> existingKey=
                idempotencyKeyRepository.findByKey(idempotencyKey);  
        
        if (existingKey.isPresent()) {
            IdempotencyKey record = existingKey.get();
            if (!record.getUserId().equals(userId)) {
                throw new BusinessException(
                        "Invalid idempotency key",
                        "INVALID_IDEMPOTENCY_KEY"
                );
           }
            //la requête a déjà été traitée, on retourne la réponse précédente
            if (record.getStatus() == IdempotencyStatus.COMPLETED) {
                CheckoutResponse response;
                try {
                    response = objectMapper.readValue(
                        record.getResponse(), 
                        CheckoutResponse.class);
                } catch (JsonProcessingException e) {
                    throw new BusinessException(
                            "Failed to parse idempotency key response",
                            "IDEMPOTENCY_KEY_RESPONSE_PARSE_ERROR"
                    );
                }
                return response;
                

            }
            //la requête est en cours de traitement
            if (record.getStatus() == IdempotencyStatus.PROCESSING) {
                throw new BusinessException(
                        "Request is already being processed",
                        "IDEMPOTENCY_KEY_PROCESSING"
                );
            }

            //la requête précédente a échoué, on peut réessayer
            if (record.getStatus() == IdempotencyStatus.FAILED) {
                idempotencyKeyRepository.delete(record);
            }
        }
        //============================================================
        // 0.2 Création d'un nouvel enregistrement d'idempotence    
        //============================================================

        IdempotencyKey newKey = IdempotencyKey.builder()
                .key(idempotencyKey)
                .userId(userId)
                .status(IdempotencyStatus.PROCESSING)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(30)) // Expire après 5 minutes
                .build();
        idempotencyKeyRepository.save(newKey);

        // ============================================================
        // 1. RÉCUPÉRATION DES DONNÉES NÉCESSAIRES
        // ============================================================

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId, "USER_NOT_FOUND"
                        )
                );

        DeliveryAddress address = deliveryAddressRepository
                .findByIdAndUserIdAndDeletedFalse(
                        request.getAddressId(),
                        userId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "DeliveryAddress not found with id: "
                                        + request.getAddressId(), "DELIVERY_ADDRESS_NOT_FOUND"
                        )
                );

        ShippingMethod shippingMethod =
                shippingMethodRepository
                        .findByIdAndActiveTrue(
                                request.getShippingMethodId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "ShippingMethod not found with id: "
                                                + request.getShippingMethodId(), "SHIPPING_METHOD_NOT_FOUND"
                                )
                        );

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Panier introuvable pour cet utilisateur", "CART_NOT_FOUND"
                        )
                );

        List<CartItem> cartItems =
                cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new BusinessException(
                    "Le panier est vide, impossible de passer la commande", "CART_IS_EMPTY"
            );
        }


        // ============================================================
        // 2. CALCUL DU SOUS-TOTAL
        // ============================================================

        BigDecimal subtotal = computeCartSubtotal(cartItems);


        // ============================================================
        // 3. CALCUL DES FRAIS DE LIVRAISON
        // ============================================================

        BigDecimal shippingCost =
                shippingCalculationService
                        .calculateShippingCost(
                                subtotal,
                                shippingMethod
                        )
                        .setScale(2, RoundingMode.HALF_UP);


        // ============================================================
        // 4. APPLICATION DU COUPON
        // ============================================================

        Coupon appliedCoupon = null;

        CouponDiscountResult discountResult =
                CouponDiscountResult.builder()
                        .discountOnSubtotal(BigDecimal.ZERO)
                        .discountOnShipping(BigDecimal.ZERO)
                        .totalDiscount(BigDecimal.ZERO)
                        .build();

        if (!isBlank(request.getCouponCode())) {

            appliedCoupon =
                    couponService.validateCoupon(
                            request.getCouponCode(),
                            user,
                            subtotal
                    );

            discountResult =
                    couponCalculationService.calculateDiscount(
                            appliedCoupon,
                            subtotal,
                            shippingCost
                    );
        }


        // ============================================================
        // 5. CALCUL DES MONTANTS FINAUX
        // ============================================================

        BigDecimal effectiveSubtotal =
                subtotal
                        .subtract(
                                discountResult.getDiscountOnSubtotal()
                        )
                        .max(BigDecimal.ZERO)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal effectiveShippingCost =
                shippingCost
                        .subtract(
                                discountResult.getDiscountOnShipping()
                        )
                        .max(BigDecimal.ZERO)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxRate =
                taxServiceImpl.getCurrentTaxRate();

        BigDecimal taxAmount =
                effectiveSubtotal
                        .multiply(taxRate)
                        .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total =
                effectiveSubtotal
                        .add(effectiveShippingCost)
                        .add(taxAmount)
                        .setScale(2, RoundingMode.HALF_UP);


        // ============================================================
        // 6. CRÉATION DE LA COMMANDE
        // ============================================================
        //
        // IMPORTANT :
        // La commande est créée dans LA MÊME TRANSACTION que la
        // réservation du stock.
        //
        // Même si orderRepository.save() est exécuté maintenant,
        // la commande ne sera réellement validée qu'au COMMIT.
        //
        // Si reserveStock() échoue plus bas :
        //
        //      → rollback
        //      → Order annulée
        //      → OrderItems annulés
        //      → réservation annulée
        //      → reservedQuantity annulée
        //

        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .dateOrder(LocalDateTime.now())
                .status(OrderStatus.PENDING)

                .user(user)
                .deliveryAddress(address)

                .subtotal(subtotal)
                .shippingOrder(effectiveShippingCost)
                .shippingCost(effectiveShippingCost)

                .taxRate(taxRate)
                .taxAmount(taxAmount)

                .discountAmount(
                        discountResult.getTotalDiscount()
                )

                .couponCodeUsed(
                        appliedCoupon != null
                                ? appliedCoupon.getCode()
                                : null
                )

                .total(total)

                .shippingMethodName(shippingMethod.getName())
                .deliveryType(shippingMethod.getDeliveryType())

                .deliveryMinDays(
                        shippingMethod.getMinDeliveryDays()
                )

                .deliveryMaxDays(
                        shippingMethod.getMaxDeliveryDays()
                )

                .shippingFirstName(
                        isBlank(address.getFirstName())
                                ? user.getFirstname()
                                : address.getFirstName()
                )

                .shippingLastName(
                        isBlank(address.getLastName())
                                ? user.getLastname()
                                : address.getLastName()
                )

                .shippingStreet(address.getAddress())
                .shippingCity(address.getCity())
                .shippingState(address.getState())
                .shippingZipCode(address.getZipcode())

                .shippingCountry(
                        isBlank(address.getCountry())
                                ? "US"
                                : address.getCountry()
                                        .toUpperCase(Locale.ROOT)
                )

                .shippingPhone(address.getPhone())

                .build();


        // ============================================================
        // 7. AJOUT DES ORDER ITEMS
        // ============================================================

        for (CartItem cartItem : cartItems) {

            Variant variant = cartItem.getVariant();

            BigDecimal unitPrice = variant.getPrice();

            BigDecimal lineTotal =
                    unitPrice
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );
            //l'image snapshot determination 

            String imageUrlSnapshot = resolveVariantImageUrl(variant);
            
            OrderItem orderItem =
                    OrderItem.builder()
                            .order(order)
                            .variant(variant)
                            .quantity(cartItem.getQuantity())
                            .unitPrice(unitPrice)
                            .totalPrice(lineTotal)
                            .productNameSnapshot(variant.getProduct().getName())
                            .variantSkuSnapshot(variant.getSku())
                            .colorSnapshot(variant.getProductColor() != null ? variant.getProductColor().getName() : null)
                            .productDescriptionSnapshot(variant.getProduct().getDescription())
                            .sizeSnapshot(variant.getSize())
                            .imageUrlSnapshot(imageUrlSnapshot)
                            .build();

            order.getOrderItems().add(orderItem);
        }


        // ============================================================
        // 8. SAUVEGARDE DE LA COMMANDE
        // ============================================================
        //
        // Toujours dans la transaction.
        //
        // Si la réservation du stock échoue ensuite,
        // cette sauvegarde sera rollbackée.
        //

        Order savedOrder =
                orderRepository.save(order);


        // ============================================================
        // 9. RÉSERVATION DU STOCK
        // ============================================================
        //
        // C'est ICI que se fait la vraie vérification finale
        // de disponibilité.
        //
        // StockService.reserveStock() utilise :
        //
        //     @Lock(PESSIMISTIC_WRITE)
        //
        // donc deux clients ne peuvent pas réserver simultanément
        // une quantité qui dépasse le stock disponible.
        //
        // reserveStock() :
        //
        //     availableQuantity =
        //          quantity - reservedQuantity
        //
        // puis :
        //
        //     reservedQuantity += quantity
        //
        // et crée :
        //
        //     StockReservation(ACTIVE)
        //
        // IMPORTANT :
        // quantity N'EST PAS diminuée ici.
        //
        // Si le stock est insuffisant :
        //
        //     → exception
        //     → rollback de toute la transaction
        //     → Order annulée
        //

        for (CartItem cartItem : cartItems) {

            Variant variant = cartItem.getVariant();

            stockService.reserveStock(
                    variant.getId(),
                    cartItem.getQuantity(),
                    savedOrder.getId()
            );
        }


        // ============================================================
        // 10. ENREGISTREMENT DE L'UTILISATION DU COUPON
        // ============================================================

        if (appliedCoupon != null) {

            couponService.registerUsage(
                    appliedCoupon,
                    user,
                    savedOrder
            );
        }


        // ============================================================
        // 11. PRÉFÉRENCE DE LIVRAISON
        // ============================================================

        DeliveryPreference preference =
                DeliveryPreference.builder()
                        .order(savedOrder)
                        .leaveAtDoor(
                                request.isLeaveAtDoor()
                        )
                        .requireSignature(
                                request.isRequireSignature()
                        )
                        .deliveryNote(
                                request.getDeliveryNote()
                        )
                        .build();

        deliveryPreferenceRepository.save(preference);


        // ============================================================
        // 12. CRÉATION DE LA SESSION STRIPE
        // ============================================================
        //
        // Si Stripe échoue :
        //
        //     → exception
        //     → rollback
        //     → Order annulée
        //     → réservation annulée
        //
        // Cela évite d'avoir une réservation sans session de paiement.
        //

        CheckoutSessionData checkoutSessionData;

        try {

            checkoutSessionData =stripePaymentService.createCheckoutSession(savedOrder);
            log.info("Session Stripe créée avec succès pour la commande {} : {}", savedOrder.getId(), checkoutSessionData.paymentIntentId());

        } catch (StripeException e) {

            e.printStackTrace();

            System.err.println(
                    e.getMessage()
                            + " "
                            + e.getCode()
                            + " "
                            + e.getStatusCode()
            );

            throw new BusinessException(
                    "Erreur lors de la création de la session de paiement Stripe",
                    "STRIPE_SESSION_CREATION_FAILED",
                    e
            );
        }


        // ============================================================
        // 13. CRÉATION DU PAYMENT EN PENDING
        // ============================================================

        log.info("Création du paiement en PENDING pour la commande {}; paymentIntentId={}", savedOrder.getId(), checkoutSessionData.paymentIntentId());

        Payment payment =
                Payment.builder()
                        .paymentMethod("CARD")
                        .amount(savedOrder.getTotal())
                        .status(PaymentStatus.PENDING)
                        .stripePaymentIntentId(
                                checkoutSessionData.paymentIntentId()
                        )
                        .order(savedOrder)
                        .stripeCheckoutSessionId(
                                checkoutSessionData.sessionId()
                        )
                        .build();

        paymentRepository.save(payment);


        // ============================================================
        // 14. COMMIT
        // ============================================================
        //
        // Si aucune exception n'est levée :
        //
        //     Order              → enregistrée
        //     OrderItems         → enregistrés
        //     reservedQuantity   → augmentée
        //     StockReservation   → ACTIVE
        //     Payment            → PENDING
        //     Coupon usage       → enregistré
        //
        // Le verrou PESSIMISTIC_WRITE est libéré au COMMIT.
        //
        // Le stock physique (quantity) n'est PAS diminué.
        //
        // La diminution définitive sera effectuée par le webhook
        // après confirmation du paiement Stripe.
        //

        return CheckoutResponse.builder()
                .paymentUrl(
                        checkoutSessionData.paymentUrl()
                )
                .orderId(savedOrder.getId())

                .build();
    }



        // ============================================================
    // ORDER MAPPING
    // ============================================================

    private Order getEntityById(Integer id) {

        return orderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with id: " + id, "ORDER_NOT_FOUND"
                        )
                );
    }

    private void applyDtoToEntity(
            OrderDto dto,
            Order entity
    ) {

        if (dto.getItems() == null
                || dto.getItems().isEmpty()) {

            throw new BusinessException(
                    "La commande doit contenir au moins un article",
                    "ORDER_MUST_HAVE_AT_LEAST_ONE_ITEM"
            );
        }

        entity.setOrderNumber(
                !isBlank(dto.getOrderNumber())
                        ? dto.getOrderNumber()
                        : generateOrderNumber()
        );

        entity.setDateOrder(
                dto.getDateOrder() != null
                        ? dto.getDateOrder()
                        : LocalDateTime.now()
        );

        BigDecimal shippingBase =
                dto.getShippingCost() != null
                        ? dto.getShippingCost()
                        : dto.getShippingOrder() != null
                                ? dto.getShippingOrder()
                                : BigDecimal.ZERO;

        entity.setShippingOrder(shippingBase);
        entity.setShippingCost(shippingBase);

        entity.setDiscountAmount(
                dto.getDiscountAmount() != null
                        ? dto.getDiscountAmount()
                        : BigDecimal.ZERO
        );

        entity.setCouponCodeUsed(
                dto.getCouponCodeUsed()
        );

        entity.setStatus(
                dto.getStatus() != null
                        ? dto.getStatus()
                        : OrderStatus.PENDING
        );

        if (dto.getDeliveryAddressId() == null) {

            throw new BusinessException(
                    "Une adresse de livraison est obligatoire "
                            + "pour valider la commande",
                    "DELIVERY_ADDRESS_REQUIRED"
            );
        }

        if (dto.getUserId() != null) {

            entity.setUser(
                    userRepository.findById(dto.getUserId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found with id: " + dto.getUserId(), "USER_NOT_FOUND"
                                    )
                            )
            );

        } else {

            entity.setUser(null);
        }

        DeliveryAddress address =
                deliveryAddressRepository
                        .findByIdAndDeletedFalse(
                                dto.getDeliveryAddressId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "DeliveryAddress not found with id: " + dto.getDeliveryAddressId(), "DELIVERY_ADDRESS_NOT_FOUND"
                                )
                        );

        entity.setDeliveryAddress(address);

        entity.setShippingMethodName(
                !isBlank(dto.getShippingMethodName())
                        ? dto.getShippingMethodName()
                        : "Manual shipping"
        );

        entity.setDeliveryType(
                dto.getDeliveryType() != null
                        ? dto.getDeliveryType()
                        : DeliveryType.HOME
        );

        entity.setDeliveryMinDays(
                dto.getDeliveryMinDays() != null
                        ? dto.getDeliveryMinDays()
                        : 0
        );

        entity.setDeliveryMaxDays(
                dto.getDeliveryMaxDays() != null
                        ? dto.getDeliveryMaxDays()
                        : 0
        );

        entity.setShippingFirstName(
                !isBlank(dto.getShippingFirstName())
                        ? dto.getShippingFirstName()
                        : address.getFirstName()
        );

        entity.setShippingLastName(
                !isBlank(dto.getShippingLastName())
                        ? dto.getShippingLastName()
                        : address.getLastName()
        );

        entity.setShippingStreet(
                !isBlank(dto.getShippingStreet())
                        ? dto.getShippingStreet()
                        : address.getAddress()
        );

        entity.setShippingCity(
                !isBlank(dto.getShippingCity())
                        ? dto.getShippingCity()
                        : address.getCity()
        );

        entity.setShippingState(
                !isBlank(dto.getShippingState())
                        ? dto.getShippingState()
                        : address.getState()
        );

        entity.setShippingZipCode(
                !isBlank(dto.getShippingZipCode())
                        ? dto.getShippingZipCode()
                        : address.getZipcode()
        );

        entity.setShippingCountry(
                !isBlank(dto.getShippingCountry())
                        ? dto.getShippingCountry()
                                .toUpperCase(Locale.ROOT)
                        : address.getCountry()
        );

        entity.setShippingPhone(
                !isBlank(dto.getShippingPhone())
                        ? dto.getShippingPhone()
                        : address.getPhone()
        );

        if (entity.getTaxRate() == null) {
            entity.setTaxRate(
                    taxServiceImpl.getCurrentTaxRate()
            );
        }

        calculateOrderItemsAndSubtotal(dto, entity);
        calculateOrderAmounts(entity);
    }


    private void calculateOrderItemsAndSubtotal(
            OrderDto dto,
            Order order
    ) {

        BigDecimal subtotal = BigDecimal.ZERO;

        order.getOrderItems().clear();

        for (OrderItemDto itemDto : dto.getItems()) {

            Variant variant =
                    variantRepository.findById(
                            itemDto.getVariantId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Variant not found with id: "
                                            + itemDto.getVariantId(), "VARIANT_NOT_FOUND"
                            )
                    );

            BigDecimal unitPrice =
                    variant.getPrice();

            BigDecimal totalPrice =
                    unitPrice
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemDto.getQuantity()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            OrderItem orderItem =
                    OrderItem.builder()
                            .order(order)
                            .variant(variant)
                            .quantity(itemDto.getQuantity())
                            .unitPrice(unitPrice)
                            .totalPrice(totalPrice)
                            .build();

            order.getOrderItems().add(orderItem);

            subtotal = subtotal.add(totalPrice);
        }

        order.setSubtotal(
                subtotal.setScale(
                        2,
                        RoundingMode.HALF_UP
                )
        );
    }


    // ============================================================
    // CALCULATIONS
    // ============================================================

    private void calculateOrderAmounts(Order order) {

        BigDecimal subtotal =
                order.getSubtotal();

        BigDecimal shippingOrder =
                order.getShippingCost() != null
                        ? order.getShippingCost()
                        : order.getShippingOrder();

        BigDecimal taxRate =
                order.getTaxRate();

        BigDecimal discountAmount =
                order.getDiscountAmount() != null
                        ? order.getDiscountAmount()
                        : BigDecimal.ZERO;

        BigDecimal taxAmount =
                subtotal
                        .multiply(taxRate)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal total =
                subtotal
                        .add(shippingOrder)
                        .add(taxAmount)
                        .subtract(discountAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        order.setShippingOrder(shippingOrder);
        order.setShippingCost(shippingOrder);
        order.setTaxAmount(taxAmount);
        order.setTotal(total);
    }


    private BigDecimal computeCartSubtotal(
            List<CartItem> cartItems
    ) {

        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem item : cartItems) {

            BigDecimal lineTotal =
                    item.getVariant()
                            .getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            )
                            .setScale(
                                    2,
                                    RoundingMode.HALF_UP
                            );

            subtotal = subtotal.add(lineTotal);
        }

        return subtotal.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }



    // ============================================================
    // DTO
    // ============================================================

    private OrderDto toDto(Order entity) {

        OrderDto dto = new OrderDto();

        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setDateOrder(entity.getDateOrder());

        dto.setTaxRate(entity.getTaxRate());
        dto.setTaxAmount(entity.getTaxAmount());

        dto.setSubtotal(entity.getSubtotal());

        dto.setShippingOrder(
                entity.getShippingOrder()
        );

        dto.setShippingCost(
                entity.getShippingCost()
        );

        dto.setShippingMethodName(
                entity.getShippingMethodName()
        );

        dto.setDeliveryType(
                entity.getDeliveryType()
        );

        dto.setDeliveryMinDays(
                entity.getDeliveryMinDays()
        );

        dto.setDeliveryMaxDays(
                entity.getDeliveryMaxDays()
        );

        dto.setShippingFirstName(
                entity.getShippingFirstName()
        );

        dto.setShippingLastName(
                entity.getShippingLastName()
        );

        dto.setShippingStreet(
                entity.getShippingStreet()
        );

        dto.setShippingCity(
                entity.getShippingCity()
        );

        dto.setShippingState(
                entity.getShippingState()
        );

        dto.setShippingZipCode(
                entity.getShippingZipCode()
        );

        dto.setShippingCountry(
                entity.getShippingCountry()
        );

        dto.setShippingPhone(
                entity.getShippingPhone()
        );

        dto.setCouponCodeUsed(
                entity.getCouponCodeUsed()
        );

        dto.setDiscountAmount(
                entity.getDiscountAmount()
        );

        dto.setTotal(entity.getTotal());
        dto.setStatus(entity.getStatus());

        dto.setUserId(
                entity.getUser() != null
                        ? entity.getUser().getId()
                        : null
        );

        dto.setDeliveryAddressId(
                entity.getDeliveryAddress() != null
                        ? entity.getDeliveryAddress().getId()
                        : null
        );

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setItems(
                entity.getOrderItems().stream()
                        .map(this::toDtoItem)
                        .toList()
        );


        return dto;
    }

    private OrderItemDto toDtoItem(OrderItem entity) {
    OrderItemDto dto = new OrderItemDto();

    // mapping des champs ici
    dto.setId(entity.getId());
    dto.setVariantId(entity.getVariant() != null ? entity.getVariant().getId() : null);
    dto.setQuantity(entity.getQuantity());
    dto.setUnitPrice(entity.getUnitPrice());
    dto.setTotalPrice(entity.getTotalPrice());
    dto.setProductNameSnapshot(entity.getProductNameSnapshot());
    dto.setVariantSkuSnapshot(entity.getVariantSkuSnapshot() );
    dto.setColorSnapshot(entity.getColorSnapshot());
    dto.setProductDescriptionSnapshot(entity.getProductDescriptionSnapshot());
    dto.setSizeSnapshot(entity.getSizeSnapshot());
    dto.setImageUrlSnapshot(entity.getImageUrlSnapshot());
    return dto;
}


    // ============================================================
    // UTILS
    // ============================================================

    private String generateOrderNumber() {

        long now = System.currentTimeMillis();

        int suffix =
                ThreadLocalRandom.current()
                        .nextInt(1000, 9999);

        return "ORD-" + now + "-" + suffix;
    }


    private boolean isBlank(String value) {

        return value == null
                || value.trim().isEmpty();
    }


    private String resolveVariantImageUrl(Variant variant) {

        // Cas 1 : la variante possède une couleur
        if (variant.getProductColor() != null) {

                ProductColor productColor = variant.getProductColor();

                if (productColor.getImages() != null) {

                return productColor.getImages()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(image ->
                                image.getDisplayOrder() != null
                                        && image.getDisplayOrder() == 1
                        )
                        .map(ProductImage::getUrl)
                        .findFirst()
                        .orElse(null);
                }
        }

        // Cas 2 : la variante n'a pas de couleur
        // On cherche l'image du produit avec displayOrder = 1
        Product product = variant.getProduct();

        if (product != null && product.getImages() != null) {

                return product.getImages()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(image ->
                                image.getDisplayOrder() != null
                                        && image.getDisplayOrder() == 1
                        )
                        .map(ProductImage::getUrl)
                        .findFirst()
                        .orElse(null);
        }

        return null;
        }
}

