package org.example.dao;

import org.example.model.Operation;
import org.hibernate.Session;

import java.util.List;

public class OperationDAO extends DAO<Operation, Long> {
    public OperationDAO() {
        super(Operation.class);
    }

    public List<Operation> findAllByUserId(Session session, Long userId) {
        return session.createQuery(
                        "FROM Operation WHERE user.id = :userId", Operation.class)
                .setParameter("userId", userId)
                .list();
    }
}
