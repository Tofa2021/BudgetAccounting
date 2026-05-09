package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
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

@RequiredArgsConstructor
public class HouseholdService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final HouseholdDAO householdDAO;
    private final HouseholdMemberDAO householdMemberDAO;
    private final UserDAO userDAO;
    private final AccountDAO accountDAO;

    public Household create(String name, Map<Long, String> startMembers, Long userId) {
        return transactionManager.executeInTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Household household = new Household();
            household.setName(name);
            household = householdDAO.save(household);

            List<HouseholdMember> members = createMembers(startMembers, household, user);
            household.setMembers(members);

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
        return householdMemberDAO.save(member);
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
        return householdDAO.findById(id)
                .orElseThrow(() -> new HouseholdNotFoundException(id));
    }

    public BigDecimal getAmount(Long id) {
        List<Account> accounts = accountDAO.getAllByHouseholdId(id);
        return accounts.stream().map(Account::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    public void update(Long id, String name) {
        transactionManager.executeInTransaction(() -> {
            Household household = householdDAO.findById(id)
                    .orElseThrow(() -> new HouseholdNotFoundException(id));
            household.setName(name);
            householdDAO.save(household);
        });
    }

    public void delete(Long id) {
        transactionManager.executeInTransaction(() -> {
            householdDAO.deleteById(id);
        });
    }
}
