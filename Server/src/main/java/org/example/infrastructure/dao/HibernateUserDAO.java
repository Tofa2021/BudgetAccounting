package org.example.infrastructure.dao;

import org.example.domain.dao.UserDAO;
import org.example.domain.model.User;
import org.example.infrastructure.transaction.HibernatePersistenceManager;

import java.util.Optional;

public class HibernateUserDAO extends HibernateDAO<User, Long> implements UserDAO {
    public HibernateUserDAO(HibernatePersistenceManager transactionManager) {
        super(User.class, transactionManager);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return HqlQueryBuilder
                .builder(User.class)
                .select()
                .where("username", "=", username)
                .build(getCurrentSession())
                .uniqueResultOptional();
    }
}
