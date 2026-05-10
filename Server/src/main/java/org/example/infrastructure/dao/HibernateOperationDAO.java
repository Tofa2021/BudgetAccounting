package org.example.infrastructure.dao;

import org.example.domain.dao.OperationDAO;
import org.example.domain.dao.OperationFilter;
import org.example.domain.model.Operation;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.List;

public class HibernateOperationDAO extends HibernateDAO<Operation, Long> implements OperationDAO {
    public HibernateOperationDAO(HibernatePersistenceManager transactionManager) {
        super(Operation.class, transactionManager);
    }

    @Override
    public List<Operation> find(OperationFilter filter) {
        var query = HqlQueryBuilder.builder(Operation.class)
                .select()
                .where("user.id", "=", filter.userId())
                .and("household.id", "=", filter.householdId())
                .and("amount", ">=", filter.minAmount())
                .and("amount", "<=", filter.maxAmount())
                .and("dateTime", ">=", filter.dateFrom())
                .and("dateTime", "<=", filter.dateTo())
                .and("category.id", "=", filter.categoryId())
                .build(getCurrentSession());

        if (filter.limit() != null) {
            query.setMaxResults(filter.limit());
        }

        return query.list();
    }
}
