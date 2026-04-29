package org.example.dao;

import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

public abstract class DAO<T, ID> {
    private final Class<T> modelClass;

    public DAO(Class<T> modelClass) {
        this.modelClass = modelClass;
    }

    public void save(Session session, T model) {
        try {
            session.persist(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findById(Session session, ID id) {
        try {
            T model = session.get(modelClass, id);
            return Optional.ofNullable(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> findAll(Session session) {
        try {
            return HqlQueryBuilder.builder(modelClass).select().build(session).list();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(Session session, T model) {
        try {
            session.merge(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(Session session, T model) {
        try {
            session.remove(model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Session session, ID id) {
        HqlQueryBuilder.builder(modelClass).delete().where("id", "=", id).buildMutation(session).executeUpdate();
    }
}
