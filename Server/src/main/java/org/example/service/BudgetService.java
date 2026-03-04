package org.example.service;

import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;
import org.example.model.Budget;
import org.example.model.DecreaseBudgetOperation;
import org.example.model.IncreaseBudgetOperation;

public class BudgetService {
    private final Budget budget = new Budget();

    public int getAmount() {
        return budget.getAmount();
    }

    public void processIncreaseOperation(IncreaseOperationRequest request) {
        IncreaseBudgetOperation operation = new IncreaseBudgetOperation(request.getAmount(), request.getCategory());
        System.out.println(request.getCategory());
        budget.setAmount(budget.getAmount() + operation.getAmount());
    }

    public void processDecreaseOperation(DecreaseOperationRequest request) {
        DecreaseBudgetOperation operation = new DecreaseBudgetOperation(request.getAmount(), request.getCategory());
        System.out.println(request.getCategory());
        budget.setAmount(budget.getAmount() - operation.getAmount());
    }
}
