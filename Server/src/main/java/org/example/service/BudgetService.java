package org.example.service;

import org.example.dto.request.OperationRequest;
import org.example.model.Budget;
import org.example.model.MinusBudgetOperation;
import org.example.model.PlusBudgetOperation;

public class BudgetService {
    private final Budget budget = new Budget();

    public int getAmount() {
        return budget.getAmount();
    }

    public void processPlusOperation(OperationRequest request) {
        PlusBudgetOperation operation = new PlusBudgetOperation(request.getAmount(), request.getCategory());
        budget.plus(operation.getAmount());
    }

    public void processMinusOperation(OperationRequest request) {
        MinusBudgetOperation operation = new MinusBudgetOperation(request.getAmount(), request.getCategory());
        budget.minus(operation.getAmount());
    }
}
