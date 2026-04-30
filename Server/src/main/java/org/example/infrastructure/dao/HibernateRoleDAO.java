package org.example.infrastructure.dao;

import org.example.domain.dao.RoleDAO;
import org.example.domain.model.Role;
import org.example.infrastructure.transaction.HibernateTransactionManager;

public class HibernateRoleDAO extends HibernateDAO<Role, Long> implements RoleDAO {
    public HibernateRoleDAO(HibernateTransactionManager transactionManager) {
        super(Role.class, transactionManager);
    }
}
