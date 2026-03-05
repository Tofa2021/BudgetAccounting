package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.Hibernate;
import org.example.TransactionUtils;
import org.example.dao.BudgetDAO;
import org.example.dao.UserDAO;
import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;
import org.example.model.Budget;
import org.example.model.DecreaseBudgetOperation;
import org.example.model.IncreaseBudgetOperation;
import org.example.model.User;
import org.hibernate.Session;

@RequiredArgsConstructor
public class BudgetService {
    private final BudgetDAO budgetDAO;
    private final UserDAO userDAO;

    public int getAmount(Long userId) {
        try (Session session = Hibernate.getSessionFactory().openSession()) {
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
            return budget.getAmount();
        }
    }

    public Budget create(Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));

            Budget budget = new Budget();
            budget.setAmount(0);
            budget.setUser(user);
            budgetDAO.save(session, budget);

            return budget;
        });
    }

    public IncreaseBudgetOperation processIncreaseOperation(IncreaseOperationRequest request) {
        return TransactionUtils.executeInTransaction(session -> {
            Long userId = request.getUserId();
            int amount = request.getAmount();
            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));

            IncreaseBudgetOperation operation = new IncreaseBudgetOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            session.persist(operation);

            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
            budget.setAmount(budget.getAmount() + amount);

            return operation;
        });
    }

    public DecreaseBudgetOperation processDecreaseOperation(DecreaseOperationRequest request) {
        return TransactionUtils.executeInTransaction(session -> {
            Long userId = request.getUserId();
            int amount = request.getAmount();

            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));

            DecreaseBudgetOperation operation = new DecreaseBudgetOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            session.persist(operation);

            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
            budget.setAmount(budget.getAmount() - amount);

            return operation;
        });
    }
}
