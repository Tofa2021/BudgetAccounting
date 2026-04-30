package org.example.domain.dao;

import org.example.domain.model.User;

import java.util.Optional;

public interface UserDAO extends DAO<User, Long> {
    Optional<User> findByUsername(String username);
}
