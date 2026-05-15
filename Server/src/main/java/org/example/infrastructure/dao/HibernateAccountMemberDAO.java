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
}
