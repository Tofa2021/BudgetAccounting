package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.LastAdminException;
import org.example.domain.exception.already_exists.HouseholdMemberAlreadyExistsExceptionException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.Household;
import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;
import org.example.domain.model.User;

@RequiredArgsConstructor
public class HouseholdMemberService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final HouseholdMemberDAO householdMemberDAO;
    private final HouseholdDAO householdDAO;
    private final UserDAO userDAO;

    public HouseholdMember create(Long householdId, String role, Long userId) {
        return transactionManager.executeInTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));

            if (householdMemberDAO.existsByUserIdAndHouseholdId(user.getId(), household.getId())) {
                throw new HouseholdMemberAlreadyExistsExceptionException(user.getId(), household.getId());
            }

            HouseholdMemberRole memberRole = HouseholdMemberRole.fromString(role);

            HouseholdMember member = new HouseholdMember();
            member.setRole(memberRole);
            member.setHousehold(household);
            member.setUser(user);
            return householdMemberDAO.save(member);
        });
    }

    public void updateRole(Long id, String newRole) {
        transactionManager.executeInTransaction(() -> {
            HouseholdMember member = householdMemberDAO.findById(id)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(id));

            checkLastAdmin(member);

            member.setRole(HouseholdMemberRole.fromString(newRole));
            householdMemberDAO.save(member);
        });
    }

    public void delete(Long id) {
        transactionManager.executeInTransaction(() -> {
            HouseholdMember member = householdMemberDAO.findById(id)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(id));

            checkLastAdmin(member);

            householdMemberDAO.delete(member);
        });
    }

    private void checkLastAdmin(HouseholdMember member) {
        if (member.getRole() == HouseholdMemberRole.ADMIN) {
            Long householdId = member.getHousehold().getId();

            long adminCount = householdMemberDAO.countByHouseholdIdAndRole(householdId, HouseholdMemberRole.ADMIN);
            if (adminCount == 1) {
                throw new LastAdminException(householdId, member.getId());
            }
        }
    }
}
