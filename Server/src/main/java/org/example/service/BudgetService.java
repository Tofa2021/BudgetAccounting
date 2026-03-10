package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.BudgetDAO;
import org.example.dao.UserDAO;
import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;
import org.example.exception.BudgetNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.model.Budget;
import org.example.model.DecreaseOperation;
import org.example.model.IncreaseOperation;
import org.example.model.User;
import org.example.util.Hibernate;
import org.example.util.TransactionUtils;
import org.hibernate.Session;

@RequiredArgsConstructor
public class BudgetService {
    private final BudgetDAO budgetDAO;
    private final UserDAO userDAO;

    public double getAmount(Long userId) {
        try (Session session = Hibernate.getSessionFactory().openSession()) {
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));
            return budget.getAmount();
        }
    }

    public Budget create(Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Budget budget = new Budget();
            budget.setAmount(0.);
            budget.setUser(user);
            budgetDAO.save(session, budget);

            return budget;
        });
    }

    public IncreaseOperation processIncreaseOperation(IncreaseOperationRequest request, Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            double amount = request.getAmount();
            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            IncreaseOperation operation = new IncreaseOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            operation.setDateTime(request.getDateTime());
            session.persist(operation);

            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));
            budget.setAmount(budget.getAmount() + amount);

            return operation;
        });
    }

    public DecreaseOperation processDecreaseOperation(DecreaseOperationRequest request, Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            double amount = request.getAmount();

            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            DecreaseOperation operation = new DecreaseOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            operation.setDateTime(request.getDateTime());
            session.persist(operation);

            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));
            budget.setAmount(budget.getAmount() - amount);

            return operation;
        });
    }
}
