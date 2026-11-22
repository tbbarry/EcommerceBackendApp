package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ReviewDtos;
import com.backend.ecommerce.entity.*;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.repository.OrderItemRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.ProductReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewServiceTest {
    @Mock ProductReviewRepository reviews;
    @Mock ProductRepository products;
    @Mock OrderItemRepository items;
    ProductReviewService service;
    User user;
    Product product;
    Order order;
    OrderItem item;

    @BeforeEach
    void setUp() {
        service = new ProductReviewService(reviews, products, items);
        user = User.builder().build();
        user.setId(1);
        product = Product.builder().name("Veste").build();
        product.setId(2);
        order = Order.builder().user(user).status(OrderStatus.DELIVERED).build();
        item = OrderItem.builder().order(order).variant(Variant.builder().product(product).build()).build();
    }

    @Test
    void onlyOwnerOfDeliveredOrderCanReview() {
        when(items.findById(3)).thenReturn(Optional.of(item));
        assertThrows(AccessDeniedException.class, () -> service.save(3, 99, new ReviewDtos.Request(5, "Bien")));
        order.setStatus(OrderStatus.SHIPPED);
        assertThrows(BusinessException.class, () -> service.save(3, 1, new ReviewDtos.Request(5, "Bien")));
        verifyNoInteractions(reviews);
    }

    @Test
    void editsExistingReviewWithoutOverridingModeration() {
        ProductReview hidden = ProductReview.builder().user(user).product(product).orderItem(item)
                .rating(1).visible(false).build();
        when(items.findById(3)).thenReturn(Optional.of(item));
        when(reviews.findByUserIdAndProductId(1, 2)).thenReturn(Optional.of(hidden));
        when(reviews.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ReviewDtos.Review result = service.save(3, 1, new ReviewDtos.Request(4, "  Très bien  "));
        assertEquals(4, result.rating());
        assertEquals("Très bien", result.comment());
        assertFalse(result.visible());
        verify(reviews).save(hidden);
    }

    @Test
    void createsVerifiedReviewFromPurchasedItemAndDeletesOnlyOwnersReview() {
        when(items.findById(3)).thenReturn(Optional.of(item));
        when(reviews.findByUserIdAndProductId(1, 2)).thenReturn(Optional.empty());
        when(reviews.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ReviewDtos.Review result = service.save(3, 1, new ReviewDtos.Request(5, "  "));
        assertTrue(result.visible());
        assertNull(result.comment());
        var saved = org.mockito.ArgumentCaptor.forClass(ProductReview.class);
        verify(reviews).save(saved.capture());
        assertSame(user, saved.getValue().getUser());
        assertSame(product, saved.getValue().getProduct());
        assertSame(item, saved.getValue().getOrderItem());

        when(reviews.findByUserIdAndProductId(1, 2)).thenReturn(Optional.of(saved.getValue()));
        service.deleteMine(3, 1);
        verify(reviews).delete(saved.getValue());
        assertThrows(AccessDeniedException.class, () -> service.deleteMine(3, 99));
    }

    @Test
    void publicSummaryIncludesOnlyVisibleRatingsAndCorrectDistribution() {
        when(products.existsById(2)).thenReturn(true);
        when(reviews.countVisibleByRating(2)).thenReturn(List.of(new Object[]{5, 2L}, new Object[]{2, 1L}));
        when(reviews.findByProductIdAndVisibleTrue(eq(2), any(Pageable.class))).thenReturn(Page.empty());
        ReviewDtos.Summary summary = service.publicReviews(2, 0);
        assertEquals(3, summary.count());
        assertEquals(4.0, summary.average());
        assertEquals(2L, summary.distribution().get(5));
        assertEquals(0L, summary.distribution().get(1));
    }
}
