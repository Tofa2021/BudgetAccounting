package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dao.AccountMemberDAO;
import org.example.domain.exception.already_exists.AccountMemberAlreadyExistsException;
import org.example.domain.exception.forbidden.RoleRequiredException;
import org.example.domain.exception.not_found.AccountMemberNotFoundException;
import org.example.domain.model.AccountMemberRole;

import java.util.Arrays;

@RequiredArgsConstructor
public class AccountPermissionChecker {
    private final AccountMemberDAO accountMemberDAO;

    public AccountMemberRole checkRole(Long accountId, Long userId, AccountMemberRole... requiredRoles) {
        AccountMemberRole role = accountMemberDAO
                .findRoleByAccountIdAndUserId(accountId, userId)
                .orElseThrow(() -> new AccountMemberNotFoundException(accountId, userId));

        if (Arrays.stream(requiredRoles).noneMatch(r -> r == role)) {
            throw new RoleRequiredException(role, requiredRoles);
        }

        return role;
    }

    public void checkMembership(Long accountId, Long userId) {
        if (!isMember(accountId, userId)) {
            throw new AccountMemberNotFoundException(userId, accountId);
        }
    }

    public void checkNoMembership(Long accountId, Long userId) {
        if (isMember(accountId, userId)) {
            throw new AccountMemberAlreadyExistsException(userId, accountId);
        }
    }

    private boolean isMember(Long accountId, Long userId) {
        return accountMemberDAO.existsByAccountIdAndUserId(accountId, userId);
    }
}
