package org.example.dao;

import org.example.model.Operation;

public class BudgetOperationDAO extends DAO<Operation, Long> {
    public BudgetOperationDAO() {
        super(Operation.class);
    }
}
