package org.example.infrastructure.dao;

import org.example.domain.dao.AccountMemberDAO;
import org.example.domain.model.AccountMember;
import org.example.domain.model.AccountMemberRole;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.List;
import java.util.Optional;

public class HibernateAccountMemberDAO extends HibernateDAO<AccountMember, Long> implements AccountMemberDAO {
    public HibernateAccountMemberDAO(HibernatePersistenceManager transactionManager) {
        super(AccountMember.class, transactionManager);
    }

    @Override
    public List<AccountMember> findByUserIdAndHouseholdId(Long userId, Long householdId) {
        return HqlQueryBuilder
                .builder(AccountMember.class)
                .select()
                .where("householdMember.user.id", "=", userId)
                .and("account.household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public boolean existsByAccountIdAndUserId(Long id, Long userId) {
        return HqlQueryBuilder
                .builder(AccountMember.class)
                .selectCount()
                .where("account.id", "=", id)
                .and("householdMember.user.id", "=", userId)
                .buildCount(getCurrentSession())
                .getSingleResultOrNull() > 0;
    }

    @Override
    public long countByAccountIdAndRole(Long accountId, AccountMemberRole role) {
        return HqlQueryBuilder
                .builder(AccountMember.class)
                .selectCount()
                .where("account.id", "=", accountId)
                .and("role", "=", role)
                .buildCount(getCurrentSession())
                .getSingleResultOrNull();
    }

    @Override
    public Optional<AccountMember> findByAccountIdAndUserId(Long accountId, Long userId) {
        return HqlQueryBuilder.builder(AccountMember.class)
                .select()
                .where("account.id", "=", accountId)
                .and("householdMember.user.id", "=", userId)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }

    @Override
    public Optional<AccountMember> findByAccountIdAndUserIdWithRelations(Long accountId, Long userId) {
        return HqlQueryBuilder.builder(AccountMember.class)
                .select()
                .leftJoinFetch("account")
                .leftJoinFetch("householdMember")
                .leftJoinFetch("householdMember.user")
                .where("account.id", "=", accountId)
                .and("householdMember.user.id", "=", userId)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }

    @Override
    public Optional<AccountMemberRole> findRoleByAccountIdAndUserId(Long accountId, Long userId) {
        String hql = "SELECT a.role FROM AccountMember a WHERE a.account.id = :accountId AND a.householdMember.user.id = :userId";

        return getCurrentSession()
                .createQuery(hql, AccountMemberRole.class)
                .setParameter("accountId", accountId)
                .setParameter("userId", userId)
                .uniqueResultOptional();
    }

    @Override
    public List<AccountMember> getAllByHouseholdIdAndUserId(Long householdId, Long userId) {
        return HqlQueryBuilder.builder(AccountMember.class)
                .select()
                .where("account.household.id", "=", householdId)
                .and("householdMember.user.id", "=", userId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Long> getIdsByUserIdAndHouseholdId(Long userId, Long householdId) {
        return HqlQueryBuilder.builder(Long.class)
                .select("account.id")
                .where("householdMember.user.id", "=", userId)
                .and("account.household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public boolean existsByAccountIdAndHouseholdMemberId(Long accountId, Long householdMemberId) {
        return HqlQueryBuilder.builder(AccountMember.class)
                .selectCount()
                .where("account.id", "=", accountId)
                .and("householdMember.id", "=", householdMemberId)
                .buildCount(getCurrentSession())
                .getSingleResultOrNull() > 0;
    }
}
