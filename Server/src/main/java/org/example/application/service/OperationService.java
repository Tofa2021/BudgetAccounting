package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.BudgetDAO;
import org.example.domain.dao.OperationDAO;
import org.example.domain.exception.BudgetNotFoundException;
import org.example.domain.exception.OperationNotFoundException;
import org.example.domain.model.Budget;
import org.example.domain.model.DecreaseOperation;
import org.example.domain.model.IncreaseOperation;
import org.example.domain.model.Operation;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
public class OperationService {
    private final OperationDAO operationDAO;
    private final BudgetDAO budgetDAO;
    private final TransactionManager transactionManager;

    public List<Operation> getAllByUserId(Long userId) {
        return transactionManager.executeInTransaction(() -> operationDAO.findAllByUserId(userId));
    }

    public List<Operation> getRecentOperations(Long userId, IntegerAuthorizedRequest request) {
        return transactionManager.executeInTransaction(() -> {
            Instant cutoff = Instant.now().minus(request.getInteger(), ChronoUnit.DAYS);
            return operationDAO.findRecentOperations(userId, cutoff);
        });
    }

    public void deleteById(AuthorizedModelIdRequest request) {
        transactionManager.executeInTransaction(() -> {
            Long id = request.getModelId();
            Operation operation = operationDAO.findById(id).orElseThrow(() -> new OperationNotFoundException(id));

            Long userId = operation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(userId)
                    .orElseThrow(() -> new BudgetNotFoundException(userId));

            if (operation instanceof IncreaseOperation) {
                budget.decrease(operation.getAmount());
            } else if (operation instanceof DecreaseOperation) {
                budget.increase(operation.getAmount());
            }

            operationDAO.deleteById(request.getModelId());
        });
    }

    public void update(UpdateRequest<OperationDTO> request) {
        transactionManager.executeInTransaction(() -> {
            OperationDTO requestOperation = request.getData();
            Long operationId = request.getData().getId();
            Operation existingOperation = operationDAO.findById(operationId)
                    .orElseThrow(() -> new OperationNotFoundException(operationId));

            Long userId = existingOperation.getUser().getId();
            Budget budget = budgetDAO.findByUserId(userId)
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
            operationDAO.update(existingOperation);
        });
    }

    public List<Operation> getFilteredOperations(Long userId, OperationFilterRequest request) {
        return transactionManager.executeInTransaction(() -> {
            if (request instanceof IncreaseOperationFilterRequest increaseRequest) {
                return operationDAO.findFilteredOperations(
                        userId,
                        increaseRequest.getMinAmount(),
                        increaseRequest.getMaxAmount(),
                        increaseRequest.getDateFrom(),
                        increaseRequest.getDateTo(),
                        increaseRequest.getCategory()
                );
            } else if (request instanceof DecreaseOperationFilterRequest decreaseRequest) {
                return operationDAO.findFilteredOperations(
                        userId,
                        decreaseRequest.getMinAmount(),
                        decreaseRequest.getMaxAmount(),
                        decreaseRequest.getDateFrom(),
                        decreaseRequest.getDateTo(),
                        decreaseRequest.getCategory()
                );
            } else {
                return operationDAO.findFilteredOperations(
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
