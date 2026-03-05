package org.example.dao;

import org.example.model.User;
import org.hibernate.Session;

import java.util.Optional;

public class UserDAO extends DAO<User, Long> {
    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByUsername(Session session, String username) {
        return session.createQuery(
                        "FROM User WHERE username = :username", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }
}
