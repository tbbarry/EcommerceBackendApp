package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.CartDto;
import com.backend.ecommerce.dto.CartItemUpsertRequest;
import com.backend.ecommerce.dto.CartLineResponse;
import com.backend.ecommerce.dto.CartViewResponse;
import com.backend.ecommerce.entity.Cart;
import com.backend.ecommerce.entity.CartItem;
import com.backend.ecommerce.entity.Product;
import com.backend.ecommerce.entity.ProductImage;
import com.backend.ecommerce.entity.Stock;
import com.backend.ecommerce.entity.User;
import com.backend.ecommerce.entity.Variant;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.CartItemRepository;
import com.backend.ecommerce.repository.CartRepository;
import com.backend.ecommerce.repository.ProductImageRepository;
import com.backend.ecommerce.repository.UserRepository;
import com.backend.ecommerce.repository.VariantRepository;
import com.backend.ecommerce.service.CartService;
import com.backend.ecommerce.service.TaxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    private final CartItemRepository cartItemRepository;
    private final VariantRepository variantRepository;
    private final ProductImageRepository productImageRepository;
    private final TaxService taxService;

    @Override
    @Transactional(readOnly = true)
    public List<CartDto> findAll() {
        return cartRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto findById(Integer id) {
        return toDto(getEntityById(id));
    }

    @Override
    public CartDto create(CartDto dto) {
        Cart entity = new Cart();
        applyDtoToEntity(dto, entity);
        return toDto(cartRepository.save(entity));
    }

    @Override
    public CartDto update(Integer id, CartDto dto) {
        Cart entity = getEntityById(id);
        applyDtoToEntity(dto, entity);
        return toDto(cartRepository.save(entity));
    }

    @Override
    public void delete(Integer id) {
        Cart entity = getEntityById(id);
        cartRepository.delete(entity);
    }

    private Cart getEntityById(Integer id) {
        return cartRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart not found with id: " + id
                        )
                );
    }

    private void applyDtoToEntity(CartDto dto, Cart entity) {
        if (dto.getUserId() != null) {
            entity.setUser(
                    userRepository.findById(dto.getUserId())
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "User not found with id: "
                                                    + dto.getUserId()
                                    )
                            )
            );
        } else {
            entity.setUser(null);
        }
    }

    private CartDto toDto(Cart entity) {
        CartDto dto = new CartDto();

        dto.setId(entity.getId());

        dto.setUserId(
                entity.getUser() != null
                        ? entity.getUser().getId()
                        : null
        );

        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCartByUserId(Integer userId) {
        return toDto(getOrCreateCart(userId));
    }

    @Override
    public CartDto addItemToCart(
            Integer userId,
            Integer variantId,
            Integer quantity
    ) {
        addOrIncrementItem(userId, variantId, quantity);
        return toDto(getOrCreateCart(userId));
    }

    @Override
    public CartDto updateItemQuantity(
            Integer userId,
            Integer cartItemId,
            Integer quantity
    ) {
        updateItemQuantityInternal(userId, cartItemId, quantity);
        return toDto(getOrCreateCart(userId));
    }

    @Override
    public void removeItemFromCart(
            Integer userId,
            Integer cartItemId
    ) {
        removeItemInternal(userId, cartItemId);
    }

    @Override
    public void clearCart(Integer userId) {
        log.info("Clearing cart for userId: {}", userId);
        clearCartInternal(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public CartViewResponse getCartViewByUserId(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        return toCartView(cart);
    }

    @Override
    public CartViewResponse addItemToCartView(
            Integer userId,
            Integer variantId,
            Integer quantity
    ) {
        addOrIncrementItem(userId, variantId, quantity);
        return toCartView(getOrCreateCart(userId));
    }

    @Override
    public CartViewResponse updateItemQuantityView(
            Integer userId,
            Integer cartItemId,
            Integer quantity
    ) {
        updateItemQuantityInternal(userId, cartItemId, quantity);
        return toCartView(getOrCreateCart(userId));
    }

    @Override
    public CartViewResponse removeItemFromCartView(
            Integer userId,
            Integer cartItemId
    ) {
        removeItemInternal(userId, cartItemId);
        return toCartView(getOrCreateCart(userId));
    }

    @Override
    public CartViewResponse clearCartView(Integer userId) {
        log.info("Clearing cart view for userId: {}", userId);
        clearCartInternal(userId);
        return toCartView(getOrCreateCart(userId));
    }

    @Override
    public CartViewResponse syncCart(
            Integer userId,
            List<CartItemUpsertRequest> items,
            boolean replaceExisting
    ) {
        Cart cart = getOrCreateCart(userId);

        if (replaceExisting) {
            cartItemRepository.deleteByCartId(cart.getId());
        }

        if (items != null && !items.isEmpty()) {

            Map<Integer, Integer> merged = items.stream()
                    .collect(Collectors.toMap(
                            CartItemUpsertRequest::getVariantId,
                            CartItemUpsertRequest::getQuantity,
                            Integer::sum,
                            HashMap::new
                    ));

            for (Map.Entry<Integer, Integer> entry : merged.entrySet()) {

                Integer variantId = entry.getKey();
                Integer quantity = entry.getValue();

                if (variantId == null
                        || quantity == null
                        || quantity <= 0) {
                    continue;
                }

                addOrIncrementItem(
                        userId,
                        variantId,
                        quantity
                );
            }
        }

        return toCartView(getOrCreateCart(userId));
    }

    /**
     * Ajoute une quantité à un article existant
     * ou crée le CartItem s'il n'existe pas.
     *
     * Le stock disponible provient exclusivement
     * de l'entité Stock.
     */
    private void addOrIncrementItem(
            Integer userId,
            Integer variantId,
            Integer quantity
    ) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(
                    "Quantity must be greater than 0",
                    "QUANTITY_INVALID"
            );
        }

        Cart cart = getOrCreateCart(userId);
        Variant variant = getVariantOrThrow(variantId);

        Stock stock = variant.getStock();

        if (stock == null
                || stock.getAvailableQuantity() <= 0) {

            throw new BusinessException(
                    "Variant is out of stock",
                    "OUT_OF_STOCK"
            );
        }

        CartItem item = cartItemRepository
                .findByCartIdAndVariantId(
                        cart.getId(),
                        variantId
                )
                .orElseGet(() -> {

                    CartItem created = new CartItem();

                    created.setCart(cart);
                    created.setVariant(variant);
                    created.setQuantity(0);

                    return created;
                });

        int targetQuantity =
                item.getQuantity() + quantity;

        if (targetQuantity >
                stock.getAvailableQuantity()) {

            throw new BusinessException(
                    "Requested quantity exceeds available stock",
                    "INSUFFICIENT_STOCK"
            );
        }

        item.setQuantity(targetQuantity);

        cartItemRepository.save(item);
    }

    /**
     * Modifie directement la quantité d'un CartItem.
     */
    private void updateItemQuantityInternal(
            Integer userId,
            Integer cartItemId,
            Integer quantity
    ) {
        if (quantity == null || quantity < 0) {
            throw new BusinessException(
                    "Quantity must be greater than or equal to 0",
                    "QUANTITY_INVALID"
            );
        }

        Cart cart = getOrCreateCart(userId);

        CartItem item = getOwnedCartItem(
                cart.getId(),
                cartItemId
        );

        if (quantity == 0) {
            cartItemRepository.delete(item);
            return;
        }

        Stock stock = item.getVariant().getStock();

        if (stock == null
                || quantity > stock.getAvailableQuantity()) {

            throw new BusinessException(
                    "Requested quantity exceeds available stock",
                    "INSUFFICIENT_STOCK"
            );
        }

        item.setQuantity(quantity);

        cartItemRepository.save(item);
    }

    private void removeItemInternal(
            Integer userId,
            Integer cartItemId
    ) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = getOwnedCartItem(
                cart.getId(),
                cartItemId
        );

        cartItemRepository.delete(item);
    }

    private void clearCartInternal(Integer userId) {
        Cart cart = getOrCreateCart(userId);

        cartItemRepository.deleteByCartId(
                cart.getId()
        );
        System.err.println("Cart cleared for userId: " + userId);
    }

    private Cart getOrCreateCart(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with id: " + userId
                        )
                );

        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {

                    Cart newCart = new Cart();

                    newCart.setUser(user);

                    return cartRepository.save(newCart);
                });
    }

    private Variant getVariantOrThrow(Integer variantId) {
        return variantRepository.findById(variantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Variant not found with id: "
                                        + variantId
                        )
                );
    }

    private CartItem getOwnedCartItem(
            Integer cartId,
            Integer cartItemId
    ) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "CartItem not found with id: "
                                        + cartItemId
                        )
                );

        if (!item.getCart()
                .getId()
                .equals(cartId)) {

            throw new BusinessException(
                    "This item does not belong to this user's cart",
                    "ITEM_NOT_OWNED"
            );
        }

        return item;
    }

    private CartViewResponse toCartView(Cart cart) {

        List<CartItem> items =
                cartItemRepository.findByCartId(
                        cart.getId()
                );

        List<Integer> productIds = items.stream()
                .map(CartItem::getVariant)
                .map(Variant::getProduct)
                .map(Product::getId)
                .distinct()
                .toList();

        Map<Integer, ProductImage> mainImagesByProductId =
                productImageRepository
                        .findByProductIdIn(productIds)
                        .stream()
                        .collect(Collectors.toMap(
                                img -> img.getProduct().getId(),
                                img -> img,
                                (current, incoming) ->
                                        current.isMain()
                                                ? current
                                                : incoming,
                                HashMap::new
                        ));

        List<CartLineResponse> lines =
                new ArrayList<>();

        int totalItems = 0;

        BigDecimal subtotal =
                BigDecimal.ZERO;

        for (CartItem item : items) {

            Variant variant = item.getVariant();

            Product product = variant.getProduct();

            ProductImage image =
                    mainImagesByProductId.get(
                            product.getId()
                    );

            BigDecimal lineTotal =
                    variant.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            item.getQuantity()
                                    )
                            );

            subtotal =
                    subtotal.add(lineTotal);

            totalItems += item.getQuantity();

            /*
             * Stock disponible réel.
             *
             * Stock.quantity = stock physique
             * Stock.reservedQuantity = stock réservé
             *
             * availableQuantity =
             * quantity - reservedQuantity
             */
            Stock stock = variant.getStock();

            int availableStock =
                    stock != null
                            ? stock.getAvailableQuantity()
                            : 0;

            boolean inStock =
                    availableStock > 0;

            lines.add(
                    new CartLineResponse(
                            item.getId(),
                            variant.getId(),
                            variant.getSku(),
                            product.getId(),
                            product.getName(),
                            product.getSlug(),
                            product.getBrand(),
                            variant.getProductColor() != null ? variant.getProductColor().getName() : null,
                            variant.getSize(),
                            item.getQuantity(),

                            // Le frontend continue
                            // de recevoir "stock"
                            availableStock,

                            // Le frontend continue
                            // de recevoir "inStock"
                            inStock,

                            variant.getPrice(),
                            lineTotal,
                            image != null
                                    ? image.getUrl()
                                    : null,
                            image != null
                                    ? image.getAlt()
                                    : null
                    )
            );
        }

        CartViewResponse response =
                new CartViewResponse();

        response.setCartId(cart.getId());

        response.setUserId(
                cart.getUser().getId()
        );

        response.setItems(lines);

        response.setTotalItems(totalItems);

        response.setSubtotal(subtotal);

        BigDecimal taxRate =
                taxService.getCurrentTaxRate();

        BigDecimal taxAmount =
                subtotal
                        .multiply(taxRate)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        BigDecimal total =
                subtotal
                        .add(taxAmount)
                        .setScale(
                                2,
                                RoundingMode.HALF_UP
                        );

        response.setTaxRate(taxRate);

        response.setTaxAmount(taxAmount);

        response.setTotal(total);

        response.setEmpty(lines.isEmpty());

        response.setUpdatedAt(
                LocalDateTime.now()
        );

        return response;
    }
}


