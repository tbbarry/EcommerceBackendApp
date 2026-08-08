package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CheckoutRequest;
import com.backend.ecommerce.dto.CouponDiscountResult;
import com.backend.ecommerce.dto.OrderCheckoutResponse;
import com.backend.ecommerce.dto.OrderDto;
import com.backend.ecommerce.dto.OrderItemDto;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Coupon;
import com.backend.ecommerce.entity.DeliveryAddress;
import com.backend.ecommerce.entity.DeliveryType;
import com.backend.ecommerce.entity.DeliveryPreference;
import com.backend.ecommerce.entity.Order;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.ShippingMethod;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.DeliveryAddressRepository;
import com.backend.ecommerce.repository.DeliveryPreferenceRepository;
import com.backend.ecommerce.repository.OrderRepository;
import com.backend.ecommerce.repository.ShippingMethodRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CouponCalculationService;
import com.backend.ecommerce.service.CouponService;
import com.backend.ecommerce.service.OrderService;
import com.backend.ecommerce.service.ShippingCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final TaxServiceImpl taxServiceImpl;
    private final ShippingCalculationService shippingCalculationService;
    private final CouponService couponService;
    private final CouponCalculationService couponCalculationService;

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DeliveryAddressRepository deliveryAddressRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final DeliveryPreferenceRepository deliveryPreferenceRepository;
    private final VariantRepository variantRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> findAll() {
        return orderRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto findById(Integer id) {
        return toDto(getEntityById(id));
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
    public BigDecimal getTotalPaidOrdersAmount() {
        return orderRepository.getTotalPaidOrdersAmount();
    }

    @Override
    public OrderCheckoutResponse checkout(Integer userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        DeliveryAddress address = deliveryAddressRepository
                .findByIdAndUserIdAndDeletedFalse(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + request.getAddressId()));

        ShippingMethod shippingMethod = shippingMethodRepository.findByIdAndActiveTrue(request.getShippingMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("ShippingMethod not found with id: " + request.getShippingMethodId()));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("Panier introuvable pour cet utilisateur"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BusinessException("Le panier est vide, impossible de passer la commande");
        }

        BigDecimal subtotal = computeCartSubtotal(cartItems);
        BigDecimal shippingCost = shippingCalculationService.calculateShippingCost(subtotal, shippingMethod)
                .setScale(2, RoundingMode.HALF_UP);

        Coupon appliedCoupon = null;
        CouponDiscountResult discountResult = CouponDiscountResult.builder()
            .discountOnSubtotal(BigDecimal.ZERO)
            .discountOnShipping(BigDecimal.ZERO)
            .totalDiscount(BigDecimal.ZERO)
            .build();

        if (!isBlank(request.getCouponCode())) {
            appliedCoupon = couponService.validateCoupon(request.getCouponCode(), user, subtotal);
            discountResult = couponCalculationService.calculateDiscount(appliedCoupon, subtotal, shippingCost);
        }

        BigDecimal effectiveSubtotal = subtotal
            .subtract(discountResult.getDiscountOnSubtotal())
            .max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);
        BigDecimal effectiveShippingCost = shippingCost
            .subtract(discountResult.getDiscountOnShipping())
            .max(BigDecimal.ZERO)
            .setScale(2, RoundingMode.HALF_UP);

        BigDecimal taxRate = taxServiceImpl.getCurrentTaxRate();
        BigDecimal taxAmount = effectiveSubtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = effectiveSubtotal.add(effectiveShippingCost).add(taxAmount).setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setDateOrder(LocalDateTime.now());
        order.setStatus("PENDING");
        order.setUser(user);
        order.setDeliveryAddress(address);
        order.setTaxRate(taxRate);
        order.setSubtotal(subtotal);
        order.setShippingOrder(effectiveShippingCost);
        order.setShippingCost(effectiveShippingCost);
        order.setDiscountAmount(discountResult.getTotalDiscount());
        order.setCouponCodeUsed(appliedCoupon != null ? appliedCoupon.getCode() : null);
        order.setTaxAmount(taxAmount);
        order.setTotal(total);

        order.setShippingMethodName(shippingMethod.getName());
        order.setDeliveryType(shippingMethod.getDeliveryType());
        order.setDeliveryMinDays(shippingMethod.getMinDeliveryDays());
        order.setDeliveryMaxDays(shippingMethod.getMaxDeliveryDays());

        order.setShippingFirstName(isBlank(address.getFirstName()) ? user.getFirstname() : address.getFirstName());
        order.setShippingLastName(isBlank(address.getLastName()) ? user.getLastname() : address.getLastName());
        order.setShippingStreet(address.getAddress());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingZipCode(address.getZipcode());
        order.setShippingCountry(isBlank(address.getCountry()) ? "US" : address.getCountry().toUpperCase(Locale.ROOT));
        order.setShippingPhone(address.getPhone());

        for (CartItem cartItem : cartItems) {
            Variant variant = cartItem.getVariant();
            validateAndDecrementStock(variant, cartItem.getQuantity());

            BigDecimal unitPrice = variant.getPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(lineTotal);

            order.getOrderItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        if (appliedCoupon != null) {
            couponService.registerUsage(appliedCoupon, user, savedOrder);
        }

        DeliveryPreference preference = new DeliveryPreference();
        preference.setOrder(savedOrder);
        preference.setLeaveAtDoor(request.isLeaveAtDoor());
        preference.setRequireSignature(request.isRequireSignature());
        preference.setDeliveryNote(request.getDeliveryNote());
        deliveryPreferenceRepository.save(preference);

        cartItemRepository.deleteByCartId(cart.getId());

        return OrderCheckoutResponse.builder()
                .orderId(savedOrder.getId())
                .orderNumber(savedOrder.getOrderNumber())
                .dateOrder(savedOrder.getDateOrder())
                .status(savedOrder.getStatus())
                .subtotal(savedOrder.getSubtotal())
                .shippingCost(savedOrder.getShippingCost())
                .taxRate(savedOrder.getTaxRate())
                .taxAmount(savedOrder.getTaxAmount())
                .total(savedOrder.getTotal())
                .couponCodeUsed(savedOrder.getCouponCodeUsed())
                .discountAmount(savedOrder.getDiscountAmount())
                .shippingMethodName(savedOrder.getShippingMethodName())
                .deliveryMinDays(savedOrder.getDeliveryMinDays())
                .deliveryMaxDays(savedOrder.getDeliveryMaxDays())
                .build();
    }

    private Order getEntityById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private void applyDtoToEntity(OrderDto dto, Order entity) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("La commande doit contenir au moins un article");
        }

        entity.setOrderNumber(!isBlank(dto.getOrderNumber()) ? dto.getOrderNumber() : generateOrderNumber());
        entity.setDateOrder(dto.getDateOrder() != null ? dto.getDateOrder() : LocalDateTime.now());
        BigDecimal shippingBase = dto.getShippingCost() != null
            ? dto.getShippingCost()
            : (dto.getShippingOrder() != null ? dto.getShippingOrder() : BigDecimal.ZERO);
        entity.setShippingOrder(shippingBase);
        entity.setShippingCost(shippingBase);
        entity.setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO);
        entity.setCouponCodeUsed(dto.getCouponCodeUsed());
        entity.setStatus(!isBlank(dto.getStatus()) ? dto.getStatus() : "PENDING");

        if (dto.getDeliveryAddressId() == null) {
            throw new BusinessException("Une adresse de livraison est obligatoire pour valider la commande");
        }

        if (dto.getUserId() != null) {
            entity.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + dto.getUserId())));
        } else {
            entity.setUser(null);
        }

        DeliveryAddress address = deliveryAddressRepository.findByIdAndDeletedFalse(dto.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("DeliveryAddress not found with id: " + dto.getDeliveryAddressId()));
        entity.setDeliveryAddress(address);

        entity.setShippingMethodName(!isBlank(dto.getShippingMethodName()) ? dto.getShippingMethodName() : "Manual shipping");
        entity.setDeliveryType(dto.getDeliveryType() != null ? dto.getDeliveryType() : DeliveryType.HOME);
        entity.setDeliveryMinDays(dto.getDeliveryMinDays() != null ? dto.getDeliveryMinDays() : 0);
        entity.setDeliveryMaxDays(dto.getDeliveryMaxDays() != null ? dto.getDeliveryMaxDays() : 0);

        entity.setShippingFirstName(!isBlank(dto.getShippingFirstName()) ? dto.getShippingFirstName() : address.getFirstName());
        entity.setShippingLastName(!isBlank(dto.getShippingLastName()) ? dto.getShippingLastName() : address.getLastName());
        entity.setShippingStreet(!isBlank(dto.getShippingStreet()) ? dto.getShippingStreet() : address.getAddress());
        entity.setShippingCity(!isBlank(dto.getShippingCity()) ? dto.getShippingCity() : address.getCity());
        entity.setShippingState(!isBlank(dto.getShippingState()) ? dto.getShippingState() : address.getState());
        entity.setShippingZipCode(!isBlank(dto.getShippingZipCode()) ? dto.getShippingZipCode() : address.getZipcode());
        entity.setShippingCountry(!isBlank(dto.getShippingCountry()) ? dto.getShippingCountry().toUpperCase(Locale.ROOT) : address.getCountry());
        entity.setShippingPhone(!isBlank(dto.getShippingPhone()) ? dto.getShippingPhone() : address.getPhone());

        if (entity.getTaxRate() == null) {
            entity.setTaxRate(taxServiceImpl.getCurrentTaxRate());
        }

        calculateOrderItemsAndSubtotal(dto, entity);
        calculateOrderAmounts(entity);
    }

    private void calculateOrderItemsAndSubtotal(OrderDto dto, Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;

        order.getOrderItems().clear();

        for (OrderItemDto itemDto : dto.getItems()) {
            Variant variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + itemDto.getVariantId()));

            BigDecimal unitPrice = variant.getPrice();

            BigDecimal totalPrice = unitPrice
                    .multiply(BigDecimal.valueOf(itemDto.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setVariant(variant);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setUnitPrice(unitPrice);
            orderItem.setTotalPrice(totalPrice);

            order.getOrderItems().add(orderItem);

            subtotal = subtotal.add(totalPrice);
        }

        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
    }

    private OrderDto toDto(Order entity) {
        OrderDto dto = new OrderDto();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setDateOrder(entity.getDateOrder());
        dto.setTaxRate(entity.getTaxRate());
        dto.setTaxAmount(entity.getTaxAmount());
        dto.setSubtotal(entity.getSubtotal());
        dto.setShippingOrder(entity.getShippingOrder());
        dto.setShippingCost(entity.getShippingCost());
        dto.setShippingMethodName(entity.getShippingMethodName());
        dto.setDeliveryType(entity.getDeliveryType());
        dto.setDeliveryMinDays(entity.getDeliveryMinDays());
        dto.setDeliveryMaxDays(entity.getDeliveryMaxDays());
        dto.setShippingFirstName(entity.getShippingFirstName());
        dto.setShippingLastName(entity.getShippingLastName());
        dto.setShippingStreet(entity.getShippingStreet());
        dto.setShippingCity(entity.getShippingCity());
        dto.setShippingState(entity.getShippingState());
        dto.setShippingZipCode(entity.getShippingZipCode());
        dto.setShippingCountry(entity.getShippingCountry());
        dto.setShippingPhone(entity.getShippingPhone());
        dto.setCouponCodeUsed(entity.getCouponCodeUsed());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setTotal(entity.getTotal());
        dto.setStatus(entity.getStatus());
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setDeliveryAddressId(entity.getDeliveryAddress() != null ? entity.getDeliveryAddress().getId() : null);
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    private void calculateOrderAmounts(Order order) {
        BigDecimal subtotal = order.getSubtotal();
        BigDecimal shippingOrder = order.getShippingCost() != null ? order.getShippingCost() : order.getShippingOrder();
        BigDecimal taxRate = order.getTaxRate();
        BigDecimal discountAmount = order.getDiscountAmount() != null ? order.getDiscountAmount() : BigDecimal.ZERO;

        BigDecimal taxAmount = subtotal
                .multiply(taxRate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = subtotal
                .add(shippingOrder)
                .add(taxAmount)
            .subtract(discountAmount)
                .setScale(2, RoundingMode.HALF_UP);

        order.setShippingOrder(shippingOrder);
        order.setShippingCost(shippingOrder);
        order.setTaxAmount(taxAmount);
        order.setTotal(total);
    }

    private BigDecimal computeCartSubtotal(List<CartItem> cartItems) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : cartItems) {
            BigDecimal lineTotal = item.getVariant().getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    .setScale(2, RoundingMode.HALF_UP);
            subtotal = subtotal.add(lineTotal);
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }

    private void validateAndDecrementStock(Variant variant, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException("La quantite commandee est invalide");
        }

        if (variant.getStock() < quantity) {
            throw new BusinessException("Stock insuffisant pour le variant " + variant.getSku());
        }

        variant.setStock(variant.getStock() - quantity);
        if (variant.getStockRef() != null) {
            variant.getStockRef().setQuantity(variant.getStock());
        }
    }

    private String generateOrderNumber() {
        long now = System.currentTimeMillis();
        int suffix = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD-" + now + "-" + suffix;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
