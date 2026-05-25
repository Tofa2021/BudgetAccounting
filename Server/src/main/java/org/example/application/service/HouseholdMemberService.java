package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.dao.PersistenceManager;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.LastRoleException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.Household;
import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;
import org.example.domain.model.User;

import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class HouseholdMemberService {
    private final PersistenceManager persistenceManager;
    private final HouseholdPermissionChecker householdPermissionChecker;
    private final HouseholdMemberDAO householdMemberDAO;
    private final HouseholdDAO householdDAO;
    private final UserDAO userDAO;

    public HouseholdMember create(Long householdId, Long userId, String role, Long creatorUserId) {
        log.debug("Creating household member with householdId = {} role = {} userId = {}", householdId, role, userId);

        return persistenceManager.executeTransaction(() -> {
            householdPermissionChecker.checkRole(householdId, creatorUserId, HouseholdMemberRole.OWNER);

            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            Household household = householdDAO.findById(householdId)
                    .orElseThrow(() -> new HouseholdNotFoundException(householdId));

            householdPermissionChecker.checkNoMembership(householdId, userId);

            HouseholdMemberRole memberRole = HouseholdMemberRole.fromString(role);

            HouseholdMember member = new HouseholdMember();
            member.setRole(memberRole);
            member.setHousehold(household);
            member.setUser(user);
            householdMemberDAO.save(member);

            log.info("Household member created with id = {} householdId = {} role = {} userId = {}", member.getId(), householdId, role, userId);
            return member;
        });
    }

    public void updateRole(Long id, String newRole, Long userId) {
        log.debug("Updating household member with id = {} newRole = {}", id, newRole);

        persistenceManager.executeTransaction(() -> {
            HouseholdMember member = householdMemberDAO.findById(id)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(id));

            Long householdId = member.getHousehold().getId();
            HouseholdMemberRole oldRole = member.getRole();

            householdPermissionChecker.checkRole(householdId, userId, HouseholdMemberRole.OWNER);

            boolean isSelfUpdate = Objects.equals(member.getUser().getId(), userId);
            if (isSelfUpdate) {
                if (isLastOwner(member)) {
                    throw new LastRoleException(member.getId(), householdId, HouseholdMemberRole.OWNER);
                }
            } else {
                if (oldRole == HouseholdMemberRole.OWNER) {
                    throw new BadParameterException("OWNER cannot update role another OWNER");
                }
            }

            member.setRole(HouseholdMemberRole.fromString(newRole));
            householdMemberDAO.save(member);
            log.info("Household member updated with id = {} oldRole = {} newRole = {}", id, oldRole, newRole);
        });
    }

    public void delete(Long id, Long userId) {
        log.debug("Deleting household member with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            HouseholdMember member = householdMemberDAO.findById(id)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(id));

            Long householdId = member.getHousehold().getId();

            boolean isSelfDelete = Objects.equals(member.getUser().getId(), userId);
            HouseholdMemberRole memberRole = member.getRole();

            if (isSelfDelete) {
                if (isLastOwner(member)) {
                    throw new LastRoleException(member.getId(), householdId, HouseholdMemberRole.OWNER);
                }
            } else {
                householdPermissionChecker.checkRole(householdId, userId, HouseholdMemberRole.OWNER);

                if (memberRole == HouseholdMemberRole.OWNER) {
                    throw new BadParameterException("Cannot delete household member because role = OWNER. Owner cannot delete another Owner");
                }
            }

            householdMemberDAO.delete(member);
            log.info("Household member deleted with id = {}", id);
        });
    }

    public List<HouseholdMember> getAllByHouseholdId(Long householdId, Long userId) {
        log.debug("Getting all household members with householdId = {}", householdId);

        return persistenceManager.executeReadOnly(() -> {
            householdPermissionChecker.checkMembership(householdId, userId);

            List<HouseholdMember> householdMembers = householdMemberDAO.getAllByHouseholdId(householdId);
            log.info("Household members gotten householdId = {} count = {}", householdId, householdMembers.size());
            return householdMembers;
        });
    }

    private boolean isLastOwner(HouseholdMember member) {
        if (member.getRole() != HouseholdMemberRole.OWNER) {
            return false;
        }

        Long householdId = member.getHousehold().getId();
        long adminCount = householdMemberDAO.countByHouseholdIdAndRole(householdId, HouseholdMemberRole.OWNER);
        return adminCount == 1;
    }
}
