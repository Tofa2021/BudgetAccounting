package org.example.domain.dao;

import org.example.domain.model.Operation;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;

import java.time.Instant;
import java.util.List;

public interface OperationDAO extends DAO<Operation, Long> {
    List<Operation> findAllByUserId(Long userId);

    List<Operation> findRecentOperations(Long userId, Instant cutoffDate);

    List<Operation> findFilteredOperations(
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            IncreaseOperationCategory category
    );

    List<Operation> findFilteredOperations(
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            DecreaseOperationCategory category
    );

    List<Operation> findFilteredOperations(
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo
    );
}
