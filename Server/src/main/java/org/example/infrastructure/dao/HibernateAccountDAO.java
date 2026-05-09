package org.example.infrastructure.dao;

import org.example.domain.dao.AccountDAO;
import org.example.domain.model.Account;
import org.example.infrastructure.transaction.HibernateTransactionManager;

import java.util.List;

public class HibernateAccountDAO extends HibernateDAO<Account, Long> implements AccountDAO {
    public HibernateAccountDAO(HibernateTransactionManager transactionManager) {
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
}
