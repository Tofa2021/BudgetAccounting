package org.example.infrastructure.dao;

import org.example.domain.dao.HouseholdMemberDAO;
import org.example.domain.model.HouseholdMember;
import org.example.domain.model.HouseholdMemberRole;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.List;
import java.util.Optional;

public class HibernateHouseholdMemberDAO extends HibernateDAO<HouseholdMember, Long> implements HouseholdMemberDAO {
    public HibernateHouseholdMemberDAO(HibernatePersistenceManager transactionManager) {
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

    @Override
    public Optional<HouseholdMemberRole> findRoleByHouseholdIdAndUserId(Long householdId, Long userId) {
        String hql = "SELECT hm.role FROM HouseholdMember hm " +
                "WHERE hm.household.id = :householdId AND hm.user.id = :userId";

        return getCurrentSession()
                .createQuery(hql, HouseholdMemberRole.class)
                .setParameter("householdId", householdId)
                .setParameter("userId", userId)
                .uniqueResultOptional();
    }

    @Override
    public List<HouseholdMember> getAllByHouseholdId(Long householdId) {
        return HqlQueryBuilder.builder(HouseholdMember.class)
                .select()
                .where("household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }
}
