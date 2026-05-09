package org.example.infrastructure.dao;

import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;
import org.example.infrastructure.transaction.HibernateTransactionManager;

import java.util.Optional;

public class HibernateHouseholdMemberDAO extends HibernateDAO<HouseholdMember, Long> implements HouseholdMemberDAO {
    public HibernateHouseholdMemberDAO(HibernateTransactionManager transactionManager) {
        super(HouseholdMember.class, transactionManager);
    }

    @Override
    public boolean existsByUserIdAndHouseholdId(Long userId, Long householdId) {
        return HqlQueryBuilder
                .builder(HouseholdMember.class)
                .selectCount()
                .where("user.id", "=", userId)
                .and("household.id", "=", householdId)
                .buildCount(getCurrentSession())
                .getSingleResultOrNull() > 0;
    }

    @Override
    public Optional<HouseholdMember> findByUserIdAndHouseholdId(Long userId, Long householdId) {
        return HqlQueryBuilder
                .builder(HouseholdMember.class)
                .select()
                .where("user.id", "=", userId)
                .and("household.id", "=", householdId)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }

    @Override
    public long countByHouseholdIdAndRole(Long householdId, HouseholdMemberRole role) {
        return HqlQueryBuilder
                .builder(HouseholdMember.class)
                .selectCount()
                .where("household.id", "=", householdId)
                .and("role", "=", role)
                .buildCount(getCurrentSession())
                .getSingleResult();
    }
}
