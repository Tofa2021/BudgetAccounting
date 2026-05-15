package org.example.infrastructure.dao;

import org.example.domain.dao.AccountDAO;
import org.example.domain.model.Account;
import org.example.infrastructure.transaction.HibernatePersistenceManager;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class HibernateAccountDAO extends HibernateDAO<Account, Long> implements AccountDAO {
    public HibernateAccountDAO(HibernatePersistenceManager transactionManager) {
        super(Account.class, transactionManager);
    }

    @Override
    public List<Account> getAllByHouseholdId(Long householdId) {
        return HqlQueryBuilder
                .builder(Account.class)
                .select()
                .where("household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public Optional<Account> findByIdWithRelations(Long id) {
        return HqlQueryBuilder
                .builder(Account.class)
                .select()
                .leftJoinFetch("household")
                .leftJoinFetch("members")
                .where("id", "=", id)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }

    @Override
    public List<Account> getAllByHouseholdIdWithRelations(Long householdId) {
        return HqlQueryBuilder
                .builder(Account.class)
                .selectDistinct()
                .leftJoinFetch("members")
                .leftJoinFetch("household")
                .where("household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public BigDecimal getMemberAccountsAmount(Long householdId, Long userId) {
        String hql = "SELECT COALESCE(SUM(a.amount), 0) FROM Account a WHERE a.household.id = :householdId " +
                "AND EXISTS (SELECT 1 FROM AccountMember am WHERE am.account.id = a.id AND am.householdMember.user.id = :userId)";

        Query<BigDecimal> query = getCurrentSession().createQuery(hql, BigDecimal.class);
        query.setParameter("householdId", householdId);
        query.setParameter("userId", userId);

        return query.getSingleResult();
    }

    @Override
    public BigDecimal getHouseholdAccountsAmount(Long householdId) {
        String hql = "SELECT COALESCE(SUM(a.amount), 0) FROM Account a WHERE a.household.id = :householdId";

        Query<BigDecimal> query = getCurrentSession().createQuery(hql, BigDecimal.class);
        query.setParameter("householdId", householdId);

        return query.getSingleResult();
    }

    @Override
    public List<Account> getMemberAccountsWithRelations(Long householdId, Long userId) {
        String hql = "SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.members LEFT JOIN FETCH a.household WHERE a.household.id = :householdId " +
                "AND EXISTS (SELECT 1 FROM AccountMember am WHERE am.account.id = a.id AND am.householdMember.user.id = :userId)";

        Query<Account> query = getCurrentSession().createQuery(hql, Account.class);
        query.setParameter("householdId", householdId);
        query.setParameter("userId", userId);

        return query.list();
    }

    @Override
    public List<Long> getAllIdsByHouseholdId(Long householdId) {
        return HqlQueryBuilder.builder(Long.class)
                .select("id")
                .where("household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }
}
