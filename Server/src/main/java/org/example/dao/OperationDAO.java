package org.example.dao;

import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;
import org.example.model.Operation;
import org.hibernate.Session;

import java.time.Instant;
import java.util.List;

public class OperationDAO extends DAO<Operation, Long> {
    public OperationDAO() {
        super(Operation.class);
    }

    public List<Operation> findAllByUserId(Session session, Long userId) {
        return HqlQueryBuilder
                .builder(Operation.class)
                .select()
                .where("user.id", "=", userId)
                .build(session)
                .list();
    }

    public List<Operation> findRecentOperations(Session session, Long userId, Instant cutoffDate) {
        return HqlQueryBuilder
                .builder(Operation.class)
                .select()
                .where("user.id", "=", userId)
                .and("dateTime", ">=", cutoffDate)
                .build(session)
                .list();
    }

    public List<Operation> findFilteredOperations(
            Session session,
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            IncreaseOperationCategory category
    ) {
        return getBaseFilteredBuilder(userId, minAmount, maxAmount, dateFrom, dateTo, "IncreaseOperation")
                .and("category", "=", category)
                .build(session)
                .list();
    }

    public List<Operation> findFilteredOperations(
            Session session,
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo,
            DecreaseOperationCategory category
    ) {
        return getBaseFilteredBuilder(userId, minAmount, maxAmount, dateFrom, dateTo, "DecreaseOperation")
                .and("category", "=", category)
                .build(session)
                .list();
    }

    public List<Operation> findFilteredOperations(
            Session session,
            Long userId,
            Double minAmount,
            Double maxAmount,
            Instant dateFrom,
            Instant dateTo
    ) {
        return getBaseFilteredBuilder(userId, minAmount, maxAmount, dateFrom, dateTo, null)
                .build(session)
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
