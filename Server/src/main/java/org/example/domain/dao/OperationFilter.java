package org.example.domain.dao;

import lombok.Builder;
import org.example.domain.model.OperationType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OperationFilter(
        List<Long> accountIds,
        List<Long> userIds,
        BigDecimal minAmount,
        BigDecimal maxAmount,
        Instant dateFrom,
        Instant dateTo,
        Long categoryId,
        OperationType type,
        Integer limit,
        String sortDirection,
        String sortBy
) {
}

