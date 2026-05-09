package org.example.infrastructure.dao;

import org.example.domain.dao.OperationDAO;
import org.example.domain.model.Operation;
import org.example.infrastructure.transaction.HibernateTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class HibernateOperationDAO extends HibernateDAO<Operation, Long> implements OperationDAO {
    public HibernateOperationDAO(HibernateTransactionManager transactionManager) {
        super(Operation.class, transactionManager);
    }

    @Override
    public List<Operation> findAllByUserId(Long userId) {
        return HqlQueryBuilder
                .builder(Operation.class)
                .select()
                .where("user.id", "=", userId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> findRecentOperations(Long userId, Instant cutoffDate) {
        return HqlQueryBuilder
                .builder(Operation.class)
                .select()
                .where("user.id", "=", userId)
                .and("dateTime", ">=", cutoffDate)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> findFilteredOperations(
            Long userId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Instant dateFrom,
            Instant dateTo,
            Long categoryId
    ) {
        return HqlQueryBuilder.builder(Operation.class)
                .select()
                .where("user.id", "=", userId)
                .and("amount", ">=", minAmount)
                .and("amount", "<=", maxAmount)
                .and("dateTime", ">=", dateFrom)
                .and("dateTime", "<=", dateTo)
                .and("category.id", "=", categoryId)
                .build(getCurrentSession())
                .list();
    }
}
