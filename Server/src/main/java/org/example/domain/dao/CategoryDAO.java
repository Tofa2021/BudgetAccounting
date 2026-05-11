package org.example.domain.dao;

import org.example.domain.model.Category;
import org.example.domain.model.OperationType;

import java.util.List;
import java.util.Optional;

public interface CategoryDAO extends DAO<Category, Long> {
    Optional<Category> findByName(String name);

    List<Category> findAllByHouseholdId(Long householdId);

    List<Category> findAllByHouseholdIdWithRelations(Long householdId);

    List<Category> findAllByHouseholdIdAndType(Long householdId, OperationType operationType);

    List<Category> findAllByHouseholdIdAndTypeWithRelations(Long householdId, OperationType operationType);

    boolean existsByHouseholdIdAndName(Long householdId, String name);
}
