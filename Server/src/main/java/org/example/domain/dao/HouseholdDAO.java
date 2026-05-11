package org.example.domain.dao;

import org.example.domain.model.Household;

import java.util.List;
import java.util.Optional;

public interface HouseholdDAO extends DAO<Household, Long> {
    List<Household> findByUserId(Long userId);

    Optional<Household> findByIdWithRelations(Long id);
}
