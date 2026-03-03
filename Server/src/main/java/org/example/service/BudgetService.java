package org.example.service;

import org.example.model.Budget;
import org.example.model.MinusBudgetOperation;
import org.example.model.PlusBudgetOperation;

public class BudgetService {
    private final Budget budget = new Budget();

    public int getAmount() {
        return budget.getAmount();
    }

    public void processPlusOperation(PlusBudgetOperation operation) {
        budget.plus(operation.getAmount());
    }

    public void processMinusOperation(MinusBudgetOperation operation) {
        budget.minus(operation.getAmount());
    }
}
