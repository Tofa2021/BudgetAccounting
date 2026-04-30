package org.example.domain.dao;

import org.example.domain.model.Budget;

import java.util.Optional;

public interface BudgetDAO extends DAO<Budget, Long> {
    Optional<Budget> findByUserId(Long userId);
}
