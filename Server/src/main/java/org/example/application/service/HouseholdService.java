package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.*;
import org.example.domain.exception.LastRoleException;
import org.example.domain.exception.not_found.HouseholdMemberNotFoundException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.Household;
import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;
import org.example.domain.model.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class HouseholdService {
    private final PersistenceManager persistenceManager;
    private final HouseholdPermissionChecker householdPermissionChecker;
    private final HouseholdDAO householdDAO;
    private final HouseholdMemberDAO householdMemberDAO;
    private final UserDAO userDAO;
    private final AccountDAO accountDAO;

    public Household create(String name, Map<Long, String> startMembers, Long userId) {
        log.debug("Creating household with name = {} startMembers = {} userId = {}", name, startMembers, userId);

        return persistenceManager.executeTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Household household = new Household();
            household.setName(name);
            household = householdDAO.save(household);

            List<HouseholdMember> members = createMembers(startMembers, household, user);
            household.setMembers(members);

            log.info("Household created with id = {} name = {} startMembersCount = {} userId = {}", household.getId(), name, members.size(), userId);
            return household;
        });
    }

    private List<HouseholdMember> createMembers(Map<Long, String> additionalMemberMap, Household household, User owner) {
        List<HouseholdMember> members = new ArrayList<>();
        members.add(createMember(owner, household, HouseholdMemberRole.OWNER));
        members.addAll(createAdditionalMembers(additionalMemberMap, household));
        return members;
    }

    private HouseholdMember createMember(
            User user,
            Household household,
            HouseholdMemberRole role
    ) {
        householdPermissionChecker.checkNoMembership(household.getId(), user.getId());

        HouseholdMember member = new HouseholdMember();
        member.setRole(role);
        member.setHousehold(household);
        member.setUser(user);
        householdMemberDAO.save(member);

        log.info("Household member created with id = {} householdId = {} userId = {} role = {}",
                member.getId(), household.getId(), user.getId(), role);
        return member;
    }

    private List<HouseholdMember> createAdditionalMembers(Map<Long, String> additionalMemberMap, Household household) {
        List<HouseholdMember> members = new ArrayList<>();
        for (var entry : additionalMemberMap.entrySet()) {
            Long userId = entry.getKey();
            String roleName = entry.getValue();

            User user = userDAO.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
            HouseholdMemberRole role = HouseholdMemberRole.fromString(roleName);

            members.add(createMember(user, household, role));
        }
        return members;
    }

    public Household get(Long id, Long userId) {
        log.debug("Getting household with id = {}", id);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            householdPermissionChecker.checkMembership(id, userId);

            Household household = householdDAO.findByIdWithRelations(id)
                    .orElseThrow(() -> new HouseholdNotFoundException(id));
            log.info("Household gotten with id = {}", id);
            return household;
        });
    }

    public List<Household> getUserHouseholds(Long userId) {
        log.debug("Getting households by userId = {}", userId);

        return persistenceManager.executeReadOnly(() -> {
            List<Household> households = householdDAO.getAllByUserId(userId);

            log.debug("Households gotten with userId = {} count = {}", userId, households.size());
            return households;
        });
    }

    public BigDecimal getAmount(Long id, Long userId) {
        log.debug("Getting amount for household with id = {}", id);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            HouseholdMemberRole role = householdMemberDAO.findRoleByHouseholdIdAndUserId(id, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, id));

            BigDecimal amount;
            if (role == HouseholdMemberRole.MEMBER) {
                amount = accountDAO.getMemberAccountsAmount(id, userId);
            } else {
                amount = accountDAO.getHouseholdAccountsAmount(id);
            }

            log.info("Amount gotten amount = {} for householdId = {} memberRole = {}", amount, id, role);
            return amount;
        });
    }

    public void update(Long id, String name, Long userId) {
        log.debug("Updating household with id = {} newName = {}", id, name);

        persistenceManager.executeTransaction(() -> {
            householdPermissionChecker.checkRole(id, userId, HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

            Household household = householdDAO.findById(id)
                    .orElseThrow(() -> new HouseholdNotFoundException(id));

            String oldName = household.getName();

            household.setName(name);
            householdDAO.save(household);
            log.info("Household updated with id = {} oldName = {} newName = {}", id, oldName, name);
        });
    }

    public void delete(Long id, Long userId) {
        log.debug("Deleting household with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            householdPermissionChecker.checkRole(id, userId, HouseholdMemberRole.OWNER);

            long ownersCount = householdMemberDAO.countByHouseholdIdAndRole(id, HouseholdMemberRole.OWNER);
            if (ownersCount == 1) {
                throw new LastRoleException(id, HouseholdMemberRole.OWNER);
            }

            householdDAO.deleteById(id);
            log.info("Household deleted with id = {}", id);
        });
    }
}
