package org.example.domain.dao;

import org.example.domain.model.Household;

import java.util.List;

public interface HouseholdDAO extends DAO<Household, Long> {
    List<Household> findByUserId(Long userId);
}
