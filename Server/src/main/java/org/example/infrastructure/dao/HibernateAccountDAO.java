package org.example.infrastructure.dao;

import org.example.domain.dao.AccountDAO;
import org.example.domain.model.Account;
import org.example.enums.Currency;
import org.example.infrastructure.transaction.HibernatePersistenceManager;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public Map<Currency, BigDecimal> getMemberAccountsAmount(Long householdId, Long userId) {
        String hql = "SELECT a.currency, COALESCE(SUM(a.amount), 0) FROM Account a " +
                "WHERE a.household.id = :householdId " +
                "AND EXISTS (SELECT 1 FROM AccountMember am " +
                "            WHERE am.account.id = a.id " +
                "            AND am.householdMember.user.id = :userId) " +
                "GROUP BY a.currency";

        Query<Object[]> query = getCurrentSession().createQuery(hql, Object[].class);
        query.setParameter("householdId", householdId);
        query.setParameter("userId", userId);

        List<Object[]> results = query.getResultList();
        Map<Currency, BigDecimal> balanceMap = new HashMap<>();

        for (Object[] row : results) {
            Currency currency = (Currency) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            balanceMap.put(currency, amount);
        }

        return balanceMap;
    }

    @Override
    public Map<Currency, BigDecimal> getHouseholdAccountsAmount(Long householdId) {
        String hql = "SELECT a.currency, COALESCE(SUM(a.amount), 0) FROM Account a " +
                "WHERE a.household.id = :householdId " +
                "GROUP BY a.currency";

        Query<Object[]> query = getCurrentSession().createQuery(hql, Object[].class);
        query.setParameter("householdId", householdId);

        List<Object[]> results = query.getResultList();
        Map<Currency, BigDecimal> balanceMap = new HashMap<>();

        for (Object[] row : results) {
            Currency currency = (Currency) row[0];
            BigDecimal amount = (BigDecimal) row[1];
            balanceMap.put(currency, amount);
        }

        return balanceMap;
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
