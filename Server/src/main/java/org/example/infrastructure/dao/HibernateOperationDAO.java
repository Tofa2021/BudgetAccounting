package org.example.infrastructure.dao;

import org.example.domain.dao.OperationDAO;
import org.example.domain.model.Operation;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;
import org.example.infrastructure.transaction.HibernateTransactionManager;

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
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            IncreaseOperationCategory category
    ) {
        return getBaseFilteredBuilder(userId, minAmount, maxAmount, dateFrom, dateTo, "IncreaseOperation")
                .and("category", "=", category)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> findFilteredOperations(
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            DecreaseOperationCategory category
    ) {
        return getBaseFilteredBuilder(userId, minAmount, maxAmount, dateFrom, dateTo, "DecreaseOperation")
                .and("category", "=", category)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> findFilteredOperations(
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo
    ) {
        return getBaseFilteredBuilder(userId, minAmount, maxAmount, dateFrom, dateTo, null)
                .build(getCurrentSession())
                .list();
    }

    private HqlQueryBuilder<Operation> getBaseFilteredBuilder(
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            String specificTableName
    ) {
        HqlQueryBuilder<Operation> builder = HqlQueryBuilder.builder(Operation.class);

        if (specificTableName == null) {
            builder.select();
        } else {
            builder.select(specificTableName);
        }

        return builder
                .where("user.id", "=", userId)
                .and("amount", ">=", minAmount)
                .and("amount", "<=", maxAmount)
                .and("dateTime", ">=", dateFrom)
                .and("dateTime", "<=", dateTo);
    }
}
