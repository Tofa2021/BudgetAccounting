package org.example.infrastructure.dao;

import org.example.domain.dao.CategoryDAO;
import org.example.domain.model.Category;
import org.example.domain.model.OperationType;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.List;
import java.util.Optional;

public class HibernateCategoryDAO extends HibernateDAO<Category, Long> implements CategoryDAO {
    public HibernateCategoryDAO(HibernatePersistenceManager transactionManager) {
        super(Category.class, transactionManager);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return HqlQueryBuilder
                .builder(Category.class)
                .select()
                .where("name", "=", name)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }

    @Override
    public List<Category> findByHouseholdId(Long householdId) {
        return HqlQueryBuilder
                .builder(Category.class)
                .select()
                .where("household.id", "=", householdId)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public List<Category> findByHouseholdIdAndType(Long householdId, OperationType operationType) {
        return HqlQueryBuilder
                .builder(Category.class)
                .select()
                .where("household.id", "=", householdId)
                .and("type", "=", operationType)
                .build(getCurrentSession())
                .list();
    }

    @Override
    public boolean existsByHouseholdIdAndName(Long householdId, String name) {
        return HqlQueryBuilder
                .builder(Category.class)
                .selectCount()
                .where("household.id", "=", householdId)
                .and("name", "=", name)
                .buildCount(getCurrentSession())
                .getSingleResultOrNull() > 0;
    }
}
