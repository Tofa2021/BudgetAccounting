package org.example.domain.dao;

import org.example.domain.model.AccountMember;
import org.example.domain.model.AccountMemberRole;

import java.util.List;
import java.util.Optional;

public interface AccountMemberDAO extends DAO<AccountMember, Long> {
    List<AccountMember> findByUserIdAndHouseholdId(Long userId, Long householdId);

    boolean existsByAccountIdAndUserId(Long id, Long userId);

    long countByAccountIdAndRole(Long accountId, AccountMemberRole role);

    Optional<AccountMember> findByAccountIdAndUserId(Long accountId, Long userId);

    Optional<AccountMember> findByAccountIdAndUserIdWithRelations(Long accountId, Long userId);

    AccountMemberRole findRoleByAccountIdAndUserId(Long accountId, Long userId);

    List<AccountMember> getAllByHouseholdIdAndUserId(Long householdId, Long userId);
}
