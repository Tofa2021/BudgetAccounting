package org.example.infrastructure.dao;

import org.example.domain.dao.OperationDAO;
import org.example.domain.dao.OperationFilter;
import org.example.domain.model.Operation;
import org.example.domain.model.OperationType;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.List;

public class HibernateOperationDAO extends HibernateDAO<Operation, Long> implements OperationDAO {
    public HibernateOperationDAO(HibernatePersistenceManager transactionManager) {
        super(Operation.class, transactionManager);
    }

    @Override
    public List<Operation> findWithRelations(OperationFilter filter) {
        var builder = HqlQueryBuilder.builder(Operation.class)
                .selectDistinct()
                .leftJoinFetch("account")
                .leftJoinFetch("category")
                .leftJoinFetch("accountMember")
                .leftJoinFetch("accountMember.householdMember");

        if (filter.accountIds() != null && !filter.accountIds().isEmpty()) {
            builder.where("account.id", "in", filter.accountIds());
        }

        if (filter.userIds() != null && !filter.userIds().isEmpty()) {
            builder.and("createdByUserId", "in", filter.userIds());
        }

        if (filter.categoryId() != null) {
            builder.and("category.id", "=", filter.categoryId());
        }

        if (filter.type() != null) {
            builder.and("category.type", "=", filter.type());
        }

        if (filter.minAmount() != null) {
            builder.and("amount", ">=", filter.minAmount());
        }

        if (filter.maxAmount() != null) {
            builder.and("amount", "<=", filter.maxAmount());
        }

        if (filter.dateFrom() != null) {
            builder.and("dateTime", ">=", filter.dateFrom());
        }

        if (filter.dateTo() != null) {
            builder.and("dateTime", "<=", filter.dateTo());
        }

        if (filter.sortBy() != null && !filter.sortBy().isBlank()) {
            if ("DESC".equalsIgnoreCase(filter.sortDirection())) {
                builder.orderByDesc(filter.sortBy());
            } else {
                builder.orderByAsc(filter.sortBy());
            }
        }

        var query = builder.build(getCurrentSession());
        if (filter.limit() != null && filter.limit() > 0) {
            query.setMaxResults(filter.limit());
        }

        return query.list();
    }

    @Override
    public List<Operation> getAllByAccountIdWithRelations(Long accountId) {
        return HqlQueryBuilder.builder(Operation.class)
                .selectDistinct()
                .leftJoinFetch("category")
                .leftJoinFetch("accountMember")
                .leftJoinFetch("accountMember.householdMember")
                .where("account.id", "=", accountId)
                .orderByDesc("dateTime")
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> getAllByHouseholdIdWithRelations(Long householdId) {
        return HqlQueryBuilder.builder(Operation.class)
                .selectDistinct()
                .leftJoinFetch("account")
                .leftJoinFetch("category")
                .leftJoinFetch("accountMember")
                .leftJoinFetch("accountMember.householdMember")
                .where("account.household.id", "=", householdId)
                .orderByDesc("dateTime")
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> getAllByUserIdAndHouseholdIdWithRelations(Long userId, Long householdId) {
        return HqlQueryBuilder.builder(Operation.class)
                .selectDistinct()
                .leftJoinFetch("account")
                .leftJoinFetch("category")
                .leftJoinFetch("accountMember")
                .leftJoinFetch("accountMember.householdMember")
                .where("createdByUserId", "=", userId)
                .and("account.household.id", "=", householdId)
                .orderByDesc("dateTime")
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Operation> getAllExpenseOperationWithRelations(Long householdId) {
        String hql = """
                SELECT DISTINCT o 
                FROM Operation o 
                JOIN FETCH o.account a
                JOIN FETCH o.accountMember am
                JOIN FETCH o.category c 
                WHERE c.type = :categoryType
                AND a.household.id = :householdId
                """;

        var query = getCurrentSession().createQuery(hql, Operation.class);
        query.setParameter("categoryType", OperationType.EXPENSE);
        query.setParameter("householdId", householdId);

        return query.list();
    }
}
