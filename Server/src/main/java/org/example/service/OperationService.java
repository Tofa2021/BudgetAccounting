package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dao.BudgetDAO;
import org.example.dao.OperationDAO;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.*;
import org.example.exception.BudgetNotFoundException;
import org.example.exception.OperationNotFoundException;
import org.example.model.Budget;
import org.example.model.DecreaseOperation;
import org.example.model.IncreaseOperation;
import org.example.model.Operation;
import org.example.util.SessionManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
public class OperationService {
    private final OperationDAO operationDAO;
    private final BudgetDAO budgetDAO;
    private final SessionManager sessionManager;

    public List<Operation> getAllByUserId(Long userId) {
        return sessionManager.executeInTransaction(session -> {
            return operationDAO.findAllByUserId(session, userId);
        });
    }

    public List<Operation> getRecentOperations(Long userId, IntegerAuthorizedRequest request) {
        return sessionManager.executeInTransaction(session -> {
            Instant cutoff = Instant.now().minus(request.getInteger(), ChronoUnit.DAYS);
            return operationDAO.findRecentOperations(session, userId, cutoff);
        });
    }

    public void deleteById(AuthorizedModelIdRequest request) {
        sessionManager.executeInTransaction(session -> {
            Long id = request.getModelId();
            Operation operation = operationDAO.findById(session, id).orElseThrow(() -> new OperationNotFoundException(id));

            Long userId = operation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            if (operation instanceof IncreaseOperation) {
                budget.decrease(operation.getAmount());
            } else if (operation instanceof DecreaseOperation) {
                budget.increase(operation.getAmount());
            }

            operationDAO.deleteById(session, request.getModelId());
        });
    }

    public void update(UpdateRequest<OperationDTO> request) {
        sessionManager.executeInTransaction(session -> {
            OperationDTO requestOperation = request.getData();
            Long operationId = request.getData().getId();
            Operation existingOperation = operationDAO.findById(session, operationId)
                    .orElseThrow(() -> new OperationNotFoundException(operationId));

            Long userId = existingOperation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(session, userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            double oldAmount = existingOperation.getAmount();
            double newAmount = requestOperation.getAmount();
            double difference = newAmount - oldAmount;

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
            operationDAO.update(session, existingOperation);
        });
    }

    public List<Operation> getFilteredOperations(Long userId, OperationFilterRequest request) {
        return sessionManager.executeInTransaction(session -> {
            if (request instanceof IncreaseOperationFilterRequest increaseRequest) {
                return operationDAO.findFilteredOperations(
                        session,
                        userId,
                        increaseRequest.getMinAmount(),
                        increaseRequest.getMaxAmount(),
                        increaseRequest.getDateFrom(),
                        increaseRequest.getDateTo(),
                        increaseRequest.getCategory()
                );
            } else if (request instanceof DecreaseOperationFilterRequest decreaseRequest) {
                return operationDAO.findFilteredOperations(
                        session,
                        userId,
                        decreaseRequest.getMinAmount(),
                        decreaseRequest.getMaxAmount(),
                        decreaseRequest.getDateFrom(),
                        decreaseRequest.getDateTo(),
                        decreaseRequest.getCategory()
                );
            } else {
                return operationDAO.findFilteredOperations(
                        session,
                        userId,
                        request.getMinAmount(),
                        request.getMaxAmount(),
                        request.getDateFrom(),
                        request.getDateTo()
                );
            }
        });
    }
}
