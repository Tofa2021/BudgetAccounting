package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.BudgetDAO;
import org.example.dao.OperationDAO;
import org.example.dto.request.AuthorizedModelIdRequest;
import org.example.exception.BudgetNotFoundException;
import org.example.exception.OperationNotFoundException;
import org.example.model.Budget;
import org.example.model.Operation;
import org.example.util.TransactionUtils;

import java.util.List;

@RequiredArgsConstructor
public class OperationService {
    private final OperationDAO operationDAO;
    private final BudgetDAO budgetDAO;

    public List<Operation> getAllByUserId(Long userId) {
        return TransactionUtils.executeInTransaction(session -> {
            return operationDAO.findAllByUserId(session, userId);
        });
    }

    public void deleteById(AuthorizedModelIdRequest request) {
        TransactionUtils.executeInTransaction(session -> {
            Long id = request.getId();
            Operation operation = operationDAO.findById(session, id).orElseThrow(() -> new OperationNotFoundException(id));

            Long userId = operation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            budget.decrease(operation.getAmount());

            operationDAO.deleteById(session, request.getId());
        });
    }
}
