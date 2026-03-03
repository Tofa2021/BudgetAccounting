package org.example.service;

import org.example.model.Budget;
import org.example.model.Operation;

public class BudgetService {
    private final Budget budget = new Budget();

    public int getAmount() {
        return budget.getAmount();
    }

    public void processOperation(Operation operation) {
        if (operation.getType().equals("Plus")) {
            budget.plus(operation.getAmount());
        } else {
            budget.minus(operation.getAmount());
        }
    }
}
