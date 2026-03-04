package org.example.dao;

import org.example.model.BudgetOperation;

public class BudgetOperationDAO extends DAO<BudgetOperation, Long> {
    public BudgetOperationDAO() {
        super(BudgetOperation.class);
    }
}
