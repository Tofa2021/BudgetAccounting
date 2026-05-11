package org.example.infrastructure.dao;

import org.example.domain.dao.DAO;
import org.example.infrastructure.transaction.HibernatePersistenceManager;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public abstract class HibernateDAO<T, ID> implements DAO<T, ID> {
    private final Class<T> modelClass;
    private final HibernatePersistenceManager transactionManager;

    public HibernateDAO(Class<T> modelClass, HibernatePersistenceManager transactionManager) {
        this.modelClass = modelClass;
        this.transactionManager = transactionManager;
    }

    public Session getCurrentSession() {
        return transactionManager.getCurrentSession();
    }

    @Override
    public T save(T model) {
        try {
            getCurrentSession().persist(model);
            return model;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        try {
            T model = getCurrentSession().get(modelClass, id);
            return Optional.ofNullable(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> getAll() {
        try {
            return HqlQueryBuilder
                    .builder(modelClass)
                    .select()
                    .build(getCurrentSession())
                    .list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(T model) {
        try {
            getCurrentSession().merge(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(T model) {
        try {
            getCurrentSession().remove(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(ID id) {
        HqlQueryBuilder
                .builder(modelClass)
                .delete()
                .where("id", "=", id)
                .buildMutation(getCurrentSession())
                .executeUpdate();
    }
}
