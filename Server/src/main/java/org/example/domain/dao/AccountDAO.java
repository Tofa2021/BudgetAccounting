package org.example.domain.dao;

import org.example.domain.model.Account;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountDAO extends DAO<Account, Long> {
    List<Account> getAllByHouseholdId(Long householdId);

    Optional<Account> findByIdWithRelations(Long id);

    List<Account> getAllByHouseholdIdWithRelations(Long householdId);

    BigDecimal getMemberAccountAmount(Long householdId, Long userId);

    BigDecimal getHouseholdAccountAmount(Long householdId);

    List<Account> getMemberAccountsWithRelations(Long householdId, Long userId);

    List<Long> getAllIdsByHouseholdId(Long householdId);
}
