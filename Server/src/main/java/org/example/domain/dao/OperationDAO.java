package org.example.domain.dao;

import org.example.domain.model.Operation;
import org.example.dto.OperationCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public interface OperationDAO extends DAO<Operation, Long> {
    List<Operation> findAllByUserId(Long userId);

    List<Operation> findRecentOperations(Long userId, Instant cutoffDate);

    List<Operation> findFilteredOperations(
            Long userId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Instant dateFrom,
            Instant dateTo,
            OperationCategory category
    );
}
