package org.example.dao;

import org.example.model.Budget;
import org.hibernate.Session;

import java.util.Optional;

public class BudgetDAO extends DAO<Budget, Long> {
    public BudgetDAO() {
        super(Budget.class);
    }

    public Optional<Budget> findByUserId(Session session, Long userId) {
        return HqlQueryBuilder
                .builder(Budget.class)
                .select()
                .where("user.id", "=", userId)
                .build(session)
                .uniqueResultOptional();
    }
}
