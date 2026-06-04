package org.example.domain.dao;

import org.example.domain.model.Operation;
import org.example.domain.model.OperationType;

import java.util.List;

public interface OperationDAO extends DAO<Operation, Long> {
    List<Operation> findWithRelations(OperationFilter filter);

    List<Operation> getAllByAccountIdWithRelations(Long accountId);

    List<Operation> getAllByHouseholdIdWithRelations(Long householdId);

    List<Operation> getAllByUserIdAndHouseholdIdWithRelations(Long userId, Long householdId);

    List<Operation> getAllOperationByTypeWithRelations(Long householdId, OperationType type);
}

