package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.HibernateUtils;
import org.example.dao.BudgetDAO;
import org.example.dao.UserDAO;
import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;
import org.example.model.Budget;
import org.example.model.DecreaseBudgetOperation;
import org.example.model.IncreaseBudgetOperation;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;

@RequiredArgsConstructor
public class BudgetService {
    private final BudgetDAO budgetDAO;
    private final UserDAO userDAO;

    public int getAmount(Long userId) {
        Budget budget = budgetDAO.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
        return budget.getAmount();
    }

    public Budget create(Long userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));

        Budget budget = new Budget();
        budget.setAmount(0);
        budget.setUser(user);
        budgetDAO.save(budget);
        return budget;
    }

    public Budget getUserBudget(Long userId) {
        return budgetDAO.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
    }

    public IncreaseBudgetOperation processIncreaseOperation(IncreaseOperationRequest request) {
        Long userId = request.getUserId();
        int amount = request.getAmount();

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));

        IncreaseBudgetOperation operation = new IncreaseBudgetOperation();
        operation.setAmount(amount);
        operation.setUser(user);
        operation.setCategory(request.getCategory());

        Budget budget = budgetDAO.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
        budget.setAmount(budget.getAmount() + amount);

        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(operation);
            session.merge(budget);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }

        return operation;
    }

    public DecreaseBudgetOperation processDecreaseOperation(DecreaseOperationRequest request) {
        Long userId = request.getUserId();
        int amount = request.getAmount();

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id = " + userId));

        DecreaseBudgetOperation operation = new DecreaseBudgetOperation();
        operation.setAmount(amount);
        operation.setUser(user);
        operation.setCategory(request.getCategory());

        Budget budget = budgetDAO.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Budget not found for user " + userId));
        budget.setAmount(budget.getAmount() - amount);

        Transaction transaction = null;
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.persist(operation);
            session.merge(budget);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }

        return operation;
    }
}
