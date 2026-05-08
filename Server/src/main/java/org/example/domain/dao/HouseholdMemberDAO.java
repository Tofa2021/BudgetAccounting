package org.example.domain.dao;

import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;

import java.util.Optional;

public interface HouseholdMemberDAO extends DAO<HouseholdMember, Long> {

    boolean existsByUserIdAndHouseholdId(Long userId, Long householdId);

    Optional<HouseholdMember> findByUserIdAndHouseholdId(Long userId, Long householdId);

    int countByHouseholdIdAndRole(Long householdId, HouseholdMemberRole role);
}
