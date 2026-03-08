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
        return session.createQuery(
                        "FROM Operation WHERE user.id = :userId", Operation.class)
                .setParameter("userId", userId)
                .list();
    }

    public List<Operation> findRecentOperations(Session session, Long userId, Instant cutoffDate) {
        return session.createQuery(
                        "SELECT o FROM Operation o WHERE o.user.id = :userId AND o.dateTime >= :cutoffDate",
                        Operation.class
                ).setParameter("userId", userId)
                .setParameter("cutoffDate", cutoffDate)
                .list();
    }

    public List<Operation> findFilteredOperations(
            Session session,
            Long userId,
            Integer minAmount,
            Integer maxAmount,
            Instant dateFrom,
            Instant dateTo,
            IncreaseOperationCategory category
    ) {
        StringBuilder hql = new StringBuilder("SELECT o FROM IncreaseOperation o WHERE o.user.id = :userId");

        if (minAmount != null) {
            hql.append(" AND o.amount >= :minAmount");
        }
        if (maxAmount != null) {
            hql.append(" AND o.amount <= :maxAmount");
        }
        if (dateFrom != null) {
            hql.append(" AND o.dateTime >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" AND o.dateTime <= :dateTo");
        }
        if (category != null) {
            hql.append(" AND o.category = :category");
        }

        var query = session.createQuery(hql.toString(), Operation.class);
        query.setParameter("userId", userId);

        if (minAmount != null) {
            query.setParameter("minAmount", minAmount);
        }
        if (maxAmount != null) {
            query.setParameter("maxAmount", maxAmount);
        }
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }
        if (category != null) {
            query.setParameter("category", category);
        }

        return query.list();
    }

    public List<Operation> findFilteredOperations(
            Session session,
            Long userId,
            Integer minAmount,
            Integer maxAmount,
            Instant dateFrom,
            Instant dateTo,
            DecreaseOperationCategory category
    ) {
        StringBuilder hql = new StringBuilder("SELECT o FROM DecreaseOperation o WHERE o.user.id = :userId");

        if (minAmount != null) {
            hql.append(" AND o.amount >= :minAmount");
        }
        if (maxAmount != null) {
            hql.append(" AND o.amount <= :maxAmount");
        }
        if (dateFrom != null) {
            hql.append(" AND o.dateTime >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" AND o.dateTime <= :dateTo");
        }
        if (category != null) {
            hql.append(" AND o.category = :category");
        }

        var query = session.createQuery(hql.toString(), Operation.class);
        query.setParameter("userId", userId);

        if (minAmount != null) {
            query.setParameter("minAmount", minAmount);
        }
        if (maxAmount != null) {
            query.setParameter("maxAmount", maxAmount);
        }
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }
        if (category != null) {
            query.setParameter("category", category);
        }

        return query.list();
    }

    public List<Operation> findFilteredOperations(
            Session session,
            Long userId,
            Integer minAmount,
            Integer maxAmount,
            Instant dateFrom,
            Instant dateTo
    ) {
        StringBuilder hql = new StringBuilder("SELECT o FROM Operation o WHERE o.user.id = :userId");

        if (minAmount != null) {
            hql.append(" AND o.amount >= :minAmount");
        }
        if (maxAmount != null) {
            hql.append(" AND o.amount <= :maxAmount");
        }
        if (dateFrom != null) {
            hql.append(" AND o.dateTime >= :dateFrom");
        }
        if (dateTo != null) {
            hql.append(" AND o.dateTime <= :dateTo");
        }

        var query = session.createQuery(hql.toString(), Operation.class);
        query.setParameter("userId", userId);

        if (minAmount != null) {
            query.setParameter("minAmount", minAmount);
        }
        if (maxAmount != null) {
            query.setParameter("maxAmount", maxAmount);
        }
        if (dateFrom != null) {
            query.setParameter("dateFrom", dateFrom);
        }
        if (dateTo != null) {
            query.setParameter("dateTo", dateTo);
        }

        return query.list();
    }
}
