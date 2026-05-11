package org.example.domain.dao;

import org.example.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDAO extends DAO<Account, Long> {
    List<Account> getAllByHouseholdId(Long householdId);

    Optional<Account> findByIdWithRelations(Long id);

    List<Account> getAllByHouseholdIdWithRelations(Long householdId);
}
