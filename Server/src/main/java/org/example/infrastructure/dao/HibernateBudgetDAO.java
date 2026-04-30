package org.example.infrastructure.dao;

import org.example.domain.dao.BudgetDAO;
import org.example.domain.model.Budget;
import org.example.infrastructure.transaction.HibernateTransactionManager;

import java.util.Optional;

public class HibernateBudgetDAO extends HibernateDAO<Budget, Long> implements BudgetDAO {
    public HibernateBudgetDAO(HibernateTransactionManager transactionManager) {
        super(Budget.class, transactionManager);
    }

    @Override
    public Optional<Budget> findByUserId(Long userId) {
        return HqlQueryBuilder
                .builder(Budget.class)
                .select()
                .where("user.id", "=", userId)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }
}
