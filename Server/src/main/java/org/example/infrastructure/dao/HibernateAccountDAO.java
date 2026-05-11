package org.example.infrastructure.dao;

import org.example.domain.dao.AccountDAO;
import org.example.domain.model.Account;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

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
}
