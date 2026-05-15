package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.exception.already_exists.HouseholdMemberAlreadyExistsException;
import org.example.domain.exception.forbidden.RoleRequiredException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.model.HouseholdMemberRole;

import java.util.Arrays;

@RequiredArgsConstructor
public class HouseholdPermissionChecker {
    private final HouseholdMemberDAO householdMemberDAO;

    public HouseholdMemberRole checkRole(Long householdId, Long userId, HouseholdMemberRole... requiredRoles) {
        HouseholdMemberRole role = householdMemberDAO
                .findRoleByHouseholdIdAndUserId(householdId, userId)
                .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

        if (Arrays.stream(requiredRoles).noneMatch(r -> r == role)) {
            throw new RoleRequiredException(role, requiredRoles);
        }

        return role;
    }

    public void checkMembership(Long householdId, Long userId) {
        if (!isMember(householdId, userId)) {
            throw new HouseholdMemberNotFoundException(userId, householdId);
        }
    }

    public void checkNoMembership(Long householdId, Long userId) {
        if (isMember(householdId, userId)) {
            throw new HouseholdMemberAlreadyExistsException(userId, householdId);
        }
    }

    private boolean isMember(Long householdId, Long userId) {
        return householdMemberDAO.existsByUserIdAndHouseholdId(userId, householdId);
    }
}
