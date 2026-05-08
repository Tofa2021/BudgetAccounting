package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.HouseholdDAO;
import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.already_exists.HouseholdMemberAlreadyExistsExceptionException;
import org.example.domain.exception.not_found.HouseholdNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.Household;
import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;
import org.example.domain.model.User;
import org.example.dto.request.ModelIdAuthorizedRequest;
import org.example.dto.request.household.CreateHouseholdRequest;
import org.example.dto.request.household.DeleteHouseholdRequest;
import org.example.dto.request.household.GetHouseholdRequest;
import org.example.dto.request.household.UpdateHouseholdRequest;

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

    public BigDecimal getAmount(ModelIdAuthorizedRequest request) {
        return householdDAO.getAmount(request.getId());
    }

    public Household create(CreateHouseholdRequest request, Long userId) {
        return transactionManager.executeInTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Household household = new Household();
            household.setName(request.getName());
            household = householdDAO.save(household);

            List<HouseholdMember> members = createMembers(request.getAdditionalMemberRoleMap(), household, user);
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

    public void update(UpdateHouseholdRequest request) {
        transactionManager.executeInTransaction(() -> {
            Household household = householdDAO.findById(request.getHouseholdId())
                    .orElseThrow(() -> new HouseholdNotFoundException(request.getHouseholdId()));
            household.setName(request.getName());
            householdDAO.save(household);
        });
    }

    public void delete(DeleteHouseholdRequest request) {
        householdDAO.deleteById(request.getId());
    }

    public Household get(GetHouseholdRequest request) {
        return householdDAO.findById(request.getHouseholdId())
                .orElseThrow(() -> new HouseholdNotFoundException(request.getHouseholdId()));
    }
}
