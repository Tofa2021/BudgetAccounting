package org.example.dao;

import org.example.model.Role;

public class RoleDAO extends DAO<Role, Long> {
    public RoleDAO() {
        super(Role.class);
    }
}
