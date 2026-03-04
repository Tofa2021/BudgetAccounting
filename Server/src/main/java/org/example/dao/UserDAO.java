package org.example.dao;

import org.example.HibernateUtils;
import org.example.model.User;
import org.hibernate.Session;

import java.util.Optional;

public class UserDAO extends DAO<User, Long> {
    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByUsername(String username) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.createQuery(
                            "FROM User WHERE username = :username", User.class)
                    .setParameter("username", username)
                    .uniqueResultOptional();
        }
    }
}
