package org.example.domain.dao;

import org.example.domain.model.Account;

import java.util.List;

public interface AccountDAO extends DAO<Account, Long> {
    List<Account> getAllByHouseholdId(Long householdId);
}
