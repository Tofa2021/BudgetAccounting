package org.example.domain.dao;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record OperationFilter(
        Long userId,
        Long householdId,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Instant dateFrom,
        Instant dateTo,
        Long categoryId,
        Integer limit
) {
}

