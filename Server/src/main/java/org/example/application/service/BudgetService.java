package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.BudgetDAO;
import org.example.domain.dao.OperationDAO;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.BudgetNotFoundException;
import org.example.domain.exception.UserNotFoundException;
import org.example.domain.model.Budget;
import org.example.domain.model.DecreaseOperation;
import org.example.domain.model.IncreaseOperation;
import org.example.domain.model.User;
import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;

@RequiredArgsConstructor
public class BudgetService {
    private final BudgetDAO budgetDAO;
    private final OperationDAO operationDAO;
    private final UserDAO userDAO;
    private final TransactionManager transactionManager;

    public double getAmount(Long userId) {
        return transactionManager.executeInTransaction(() -> {
            Budget budget = budgetDAO.findByUserId(userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));
            return budget.getAmount();
        });
    }

    public void processIncreaseOperation(IncreaseOperationRequest request, Long userId) {
        transactionManager.executeInTransaction(() -> {
            double amount = request.getAmount();
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Budget budget = budgetDAO.findByUserId(userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            IncreaseOperation operation = new IncreaseOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            operation.setDateTime(request.getDateTime());
            operationDAO.save(operation);

            budget.setAmount(budget.getAmount() + amount);
        });
    }

    public void processDecreaseOperation(DecreaseOperationRequest request, Long userId) {
        transactionManager.executeInTransaction(() -> {
            double amount = request.getAmount();

            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Budget budget = budgetDAO.findByUserId(userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            DecreaseOperation operation = new DecreaseOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            operation.setDateTime(request.getDateTime());
            operationDAO.save(operation);

            budget.setAmount(budget.getAmount() - amount);
        });
    }
}
