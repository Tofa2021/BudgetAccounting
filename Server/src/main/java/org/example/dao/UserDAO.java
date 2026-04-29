package org.example.dao;

import org.example.model.User;
import org.hibernate.Session;

import java.util.Optional;

public class UserDAO extends DAO<User, Long> {
    public UserDAO() {
        super(User.class);
    }

    public Optional<User> findByUsername(Session session, String username) {
        return HqlQueryBuilder
                .builder(User.class)
                .select()
                .where("username", "=", username)
                .build(session)
                .uniqueResultOptional();
    }
}
