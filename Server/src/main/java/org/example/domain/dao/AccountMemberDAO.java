package org.example.domain.dao;

import org.example.domain.model.AccountMember;
import org.example.domain.model.AccountMemberRole;

import java.util.List;

public interface AccountMemberDAO extends DAO<AccountMember, Long> {
    List<AccountMember> findByUserIdAndHouseholdId(Long userId, Long householdId);

    boolean existsByAccountIdAndUserId(Long id, Long userId);

    int countByAccountIdAndRole(Long accountId, AccountMemberRole role);
}
