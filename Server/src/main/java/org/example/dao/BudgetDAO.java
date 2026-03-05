package org.example.dao;

import org.example.model.Budget;
import org.hibernate.Session;

import java.util.Optional;

public class BudgetDAO extends DAO<Budget, Long> {
    public BudgetDAO() {
        super(Budget.class);
    }

    public Optional<Budget> findByUserId(Session session, Long userId) {
        Budget budget = session.createQuery(
                        "FROM Budget WHERE user.id = :userId", Budget.class)
                .setParameter("userId", userId)
                .uniqueResult();
        return Optional.ofNullable(budget);
    }
}
