package com.backend.ecommerce.service.impl;

import com.backend.ecommerce.dto.ReviewDtos;
import com.backend.ecommerce.entity.OrderItem;
import com.backend.ecommerce.entity.ProductReview;
import com.backend.ecommerce.enums.OrderStatus;
import com.backend.ecommerce.exception.BusinessException;
import com.backend.ecommerce.exception.ResourceNotFoundException;
import com.backend.ecommerce.repository.OrderItemRepository;
import com.backend.ecommerce.repository.ProductRepository;
import com.backend.ecommerce.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductReviewService {
    private final ProductReviewRepository reviews;
    private final ProductRepository products;
    private final OrderItemRepository orderItems;

    @Transactional(readOnly = true)
    public ReviewDtos.Summary publicReviews(Integer productId, int page) {
        if (!products.existsById(productId)) {
            throw new ResourceNotFoundException("Produit introuvable");
        }
        Map<Integer, Long> distribution = new LinkedHashMap<>();
        for (int rating = 1; rating <= 5; rating++) distribution.put(rating, 0L);
        for (Object[] row : reviews.countVisibleByRating(productId)) {
            distribution.put((Integer) row[0], (Long) row[1]);
        }
        long count = distribution.values().stream().mapToLong(Long::longValue).sum();
        long total = distribution.entrySet().stream().mapToLong(e -> e.getKey() * e.getValue()).sum();
        return new ReviewDtos.Summary(count, count == 0 ? 0 : (double) total / count, distribution,
                reviews.findByProductIdAndVisibleTrue(productId, page(page)).map(this::dto));
    }

    @Transactional(readOnly = true)
    public ReviewDtos.Review mine(Integer itemId, Integer userId) {
        OrderItem item = eligibleItem(itemId, userId);
        return reviews.findByUserIdAndProductId(userId, item.getVariant().getProduct().getId())
                .map(this::dto).orElse(null);
    }

    @Transactional
    public ReviewDtos.Review save(Integer itemId, Integer userId, ReviewDtos.Request request) {
        OrderItem item = eligibleItem(itemId, userId);
        var product = item.getVariant().getProduct();
        ProductReview review = reviews.findByUserIdAndProductId(userId, product.getId())
                .orElseGet(() -> ProductReview.builder().user(item.getOrder().getUser())
                        .product(product).orderItem(item).visible(true).build());
        review.setRating(request.rating());
        String comment = request.comment();
        review.setComment(comment == null || comment.isBlank() ? null : comment.trim());
        return dto(reviews.save(review));
    }

    @Transactional
    public void deleteMine(Integer itemId, Integer userId) {
        OrderItem item = eligibleItem(itemId, userId);
        ProductReview review = reviews.findByUserIdAndProductId(userId, item.getVariant().getProduct().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Avis introuvable"));
        reviews.delete(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDtos.Review> adminReviews(int page) {
        return reviews.findAll(page(page)).map(this::dto);
    }

    @Transactional
    public ReviewDtos.Review setVisibility(Integer id, boolean visible) {
        ProductReview review = reviews.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis introuvable"));
        review.setVisible(visible);
        return dto(reviews.save(review));
    }

    private OrderItem eligibleItem(Integer itemId, Integer userId) {
        OrderItem item = orderItems.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Article introuvable"));
        if (!item.getOrder().getUser().getId().equals(userId)) throw new AccessDeniedException("Accès refusé");
        if (item.getOrder().getStatus() != OrderStatus.DELIVERED) {
            throw new BusinessException("Seuls les articles livrés peuvent être évalués", "REVIEW_ORDER_NOT_DELIVERED");
        }
        return item;
    }

    private PageRequest page(int page) {
        if (page < 0) throw new IllegalArgumentException("Page invalide");
        return PageRequest.of(page, 10, Sort.by(Sort.Order.desc("createdAt"), Sort.Order.desc("id")));
    }

    private ReviewDtos.Review dto(ProductReview review) {
        return new ReviewDtos.Review(review.getId(), review.getProduct().getName(), review.getRating(),
                review.getComment(), review.getCreatedAt(), review.isVisible());
    }
}
