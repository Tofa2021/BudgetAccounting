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
import org.example.util.SessionManager;
import org.example.util.TransactionUtils;
import org.hibernate.Session;

@RequiredArgsConstructor
public class BudgetService {
    private final BudgetDAO budgetDAO;
    private final UserDAO userDAO;
    private final SessionManager sessionManager;

    public double getAmount(Long userId) {
        try (Session session = sessionManager.openSession()) {
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));
            return budget.getAmount();
        }
    }

    public void processIncreaseOperation(IncreaseOperationRequest request, Long userId) {
        TransactionUtils.executeInTransaction(sessionManager, session -> {
            double amount = request.getAmount();
            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            IncreaseOperation operation = new IncreaseOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            operation.setDateTime(request.getDateTime());
            session.persist(operation);

            budget.setAmount(budget.getAmount() + amount);
        });
    }

    public void processDecreaseOperation(DecreaseOperationRequest request, Long userId) {
        TransactionUtils.executeInTransaction(sessionManager, session -> {
            double amount = request.getAmount();

            User user = userDAO.findById(session, userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));

            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            DecreaseOperation operation = new DecreaseOperation();
            operation.setAmount(amount);
            operation.setUser(user);
            operation.setCategory(request.getCategory());
            operation.setDateTime(request.getDateTime());
            session.persist(operation);

            budget.setAmount(budget.getAmount() - amount);
        });
    }
}
