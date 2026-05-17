package org.example.domain.dao;

import org.example.domain.model.Account;
import org.example.enums.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AccountDAO extends DAO<Account, Long> {
    List<Account> getAllByHouseholdId(Long householdId);

    Optional<Account> findByIdWithRelations(Long id);

    List<Account> getAllByHouseholdIdWithRelations(Long householdId);

    Map<Currency, BigDecimal> getMemberAccountsAmount(Long householdId, Long userId);

    Map<Currency, BigDecimal> getHouseholdAccountsAmount(Long householdId);

    List<Account> getMemberAccountsWithRelations(Long householdId, Long userId);

    List<Long> getAllIdsByHouseholdId(Long householdId);
}
