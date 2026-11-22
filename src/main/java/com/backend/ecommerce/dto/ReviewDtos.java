package com.backend.ecommerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Map;

public final class ReviewDtos {
    private ReviewDtos() {}

    public record Request(@NotNull @Min(1) @Max(5) Integer rating, @Size(max = 2000) String comment) {}
    public record VisibilityRequest(@NotNull Boolean visible) {}
    public record Review(Integer id, String productName, Integer rating, String comment,
                         LocalDateTime createdAt, boolean visible) {}
    public record Summary(long count, double average, Map<Integer, Long> distribution, Page<Review> reviews) {}
}
