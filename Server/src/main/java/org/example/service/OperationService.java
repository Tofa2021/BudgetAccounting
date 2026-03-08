package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.BudgetDAO;
import org.example.dao.OperationDAO;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.AuthorizedModelIdRequest;
import org.example.dto.request.IntegerAuthorizedRequest;
import org.example.dto.request.UpdateRequest;
import org.example.exception.BudgetNotFoundException;
import org.example.exception.OperationNotFoundException;
import org.example.model.Budget;
import org.example.model.DecreaseOperation;
import org.example.model.IncreaseOperation;
import org.example.model.Operation;
import org.example.util.TransactionUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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

    public List<Operation> getRecentOperations(Long userId, IntegerAuthorizedRequest request) {
        return TransactionUtils.executeInTransaction(session -> {
            Instant cutoff = Instant.now().minus(request.getInteger(), ChronoUnit.DAYS);
            return operationDAO.findRecentOperations(session, userId, cutoff);
        });
    }

    public void deleteById(AuthorizedModelIdRequest request) {
        TransactionUtils.executeInTransaction(session -> {
            Long id = request.getId();
            Operation operation = operationDAO.findById(session, id).orElseThrow(() -> new OperationNotFoundException(id));

            Long userId = operation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            if (operation instanceof IncreaseOperation) {
                budget.decrease(operation.getAmount());
            } else if (operation instanceof DecreaseOperation) {
                budget.increase(operation.getAmount());
            }

            operationDAO.deleteById(session, request.getId());
        });
    }

    public void update(UpdateRequest<OperationDTO> request) {
        TransactionUtils.executeInTransaction(session -> {
            OperationDTO requestOperation = request.getData();
            System.out.println(requestOperation.getAmount());
            Long operationId = request.getData().getId();
            Operation existingOperation = operationDAO.findById(session, operationId)
                    .orElseThrow(() -> new OperationNotFoundException(operationId));

            Long userId = existingOperation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            int oldAmount = existingOperation.getAmount();
            int newAmount = requestOperation.getAmount();
            int difference = newAmount - oldAmount;

            if (
                    existingOperation instanceof IncreaseOperation existingIncreaseOperation &&
                            requestOperation instanceof IncreaseOperationDTO requestIncreaseOperationDTO
            ) {
                existingIncreaseOperation.setAmount(newAmount);
                existingIncreaseOperation.setCategory(requestIncreaseOperationDTO.getCategory());
                budget.increase(difference);
            } else if (
                    existingOperation instanceof DecreaseOperation existingDecreaseOperation &&
                            requestOperation instanceof DecreaseOperationDTO requestDecreaseOperationDTO
            ) {
                existingDecreaseOperation.setAmount(newAmount);
                existingDecreaseOperation.setCategory(requestDecreaseOperationDTO.getCategory());
                budget.increase(-difference);
            } else {
                throw new IllegalArgumentException(
                        String.format("Cannot change operation type from %s to %s",
                                existingOperation.getClass().getSimpleName(),
                                request.getClass().getSimpleName()));
            }
            System.out.println(existingOperation.getAmount());
            operationDAO.update(session, existingOperation);
        });
    }
}
