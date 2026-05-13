package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.PersistenceManager;
import org.example.domain.dao.AccountDAO;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.already_exists.HouseholdMemberAlreadyExistsExceptionException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class HouseholdService { // TODO check rights
    private final PersistenceManager persistenceManager;
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
        members.add(createMember(owner, household, HouseholdMemberRole.ADMIN));
        members.addAll(createAdditionalMembers(additionalMemberMap, household));
        return members;
    }

    private HouseholdMember createMember(
            User user,
            Household household,
            HouseholdMemberRole role
    ) {
        if (householdMemberDAO.existsByUserIdAndHouseholdId(user.getId(), household.getId())) {
            throw new HouseholdMemberAlreadyExistsExceptionException(user.getId(), household.getId());
        }

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

    public Household get(Long id) {
        log.debug("Getting household with id = {}", id);

        return persistenceManager.executeReadOnly(() -> {
            Household household = householdDAO.findByIdWithRelations(id).orElseThrow(() -> new HouseholdNotFoundException(id));
            log.info("Household gotten with id = {}", id);
            return household;
        });
    }

    public BigDecimal getAmount(Long id) {
        log.debug("Getting amount for household with id = {}", id);

        return persistenceManager.executeReadOnly(() -> {
            List<Account> accounts = accountDAO.getAllByHouseholdId(id);
            BigDecimal amount = accounts.stream().map(Account::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
            log.info("Amount gotten amount = {} for householdId = {}", amount, id);
            return amount;
        });
    }

    public void update(Long id, String name) {
        log.debug("Updating household with id = {} newName = {}", id, name);

        persistenceManager.executeTransaction(() -> {
            Household household = householdDAO.findById(id)
                    .orElseThrow(() -> new HouseholdNotFoundException(id));

            String oldName = household.getName();

            household.setName(name);
            householdDAO.save(household);
            log.info("Household updated with id = {} oldName = {} newName = {}", id, oldName, name);
        });
    }

    public void delete(Long id) {
        log.debug("Deleting household with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            householdDAO.deleteById(id);
            log.info("Household deleted with id = {}", id);
        });
    }
}
