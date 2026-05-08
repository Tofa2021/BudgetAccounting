package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.TransactionManager;
import org.example.domain.dao.AccountDAO;
import org.example.domain.dao.CategoryDAO;
import org.example.domain.dao.OperationDAO;
import org.example.domain.dao.UserDAO;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.CategoryNotFoundException;
import org.example.domain.exception.not_found.OperationNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.*;
import org.example.dto.request.IntegerAuthorizedRequest;
import org.example.dto.request.operation.DeleteOperationRequest;
import org.example.dto.request.operation.OperationFilterRequest;
import org.example.dto.request.operation.OperationRequest;
import org.example.dto.request.operation.UpdateOperationRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RequiredArgsConstructor
public class OperationService { // TODO check rights and TODO logging
    private final TransactionManager transactionManager;
    private final OperationDAO operationDAO;
    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final UserDAO userDAO;

    public List<Operation> getAllByUserId(Long userId) {
        return transactionManager.executeInTransaction(() -> operationDAO.findAllByUserId(userId));
    }

    public List<Operation> getRecentOperations(Long userId, IntegerAuthorizedRequest request) {
        return transactionManager.executeInTransaction(() -> {
            Instant cutoff = Instant.now().minus(request.getInteger(), ChronoUnit.DAYS);
            return operationDAO.findRecentOperations(userId, cutoff);
        });
    }

    public void create(OperationRequest request, Long userId) {
        transactionManager.executeInTransaction(() -> {
            BigDecimal amount = request.getAmount();
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            Account account = accountDAO.findById(request.getAccountId())
                    .orElseThrow(() -> new AccountNotFoundException(request.getAccountId()));
            Category category = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

            if (!account.getHousehold().getId().equals(category.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            Operation operation = new Operation();
            operation.setAccount(account);
            operation.setUser(user);
            operation.setDescription(request.getDescription());
            operation.setCategory(category);
            operation.setAmount(request.getAmount());
            operation.setDateTime(request.getDateTime());
            operationDAO.save(operation);

            if (category.getType() == OperationType.INCOME) {
                account.increase(amount);
            } else {
                account.decrease(amount);
            }
            accountDAO.save(account);
        });
    }

    public void deleteById(DeleteOperationRequest request) {
        transactionManager.executeInTransaction(() -> {
            Operation operation = operationDAO.findById(request.getId())
                    .orElseThrow(() -> new OperationNotFoundException(request.getId()));

            Account account = operation.getAccount();

            OperationType operationType = operation.getCategory().getType();
            if (operationType == OperationType.INCOME) {
                account.decrease(operation.getAmount());
            } else if (operationType == OperationType.EXPENSE) {
                account.increase(operation.getAmount());
            }
            operationDAO.delete(operation);
            accountDAO.save(account);
        });
    }

    private void handleAccountAmountChanging(
            Account account,
            OperationType existingType,
            OperationType requestType,
            BigDecimal existingAmount,
            BigDecimal requestAmount
    ) {
        if (existingType == OperationType.EXPENSE) {
            account.increase(existingAmount);
        } else {
            account.decrease(existingAmount);
        }

        if (requestType == OperationType.EXPENSE) {
            account.decrease(requestAmount);
        } else {
            account.increase(requestAmount);
        }
    }

    public void update(UpdateOperationRequest request) {
        transactionManager.executeInTransaction(() -> {
            Operation existingOperation = operationDAO.findById(request.getId())
                    .orElseThrow(() -> new OperationNotFoundException(request.getId()));
            Account account = existingOperation.getAccount();

            BigDecimal oldAmount = existingOperation.getAmount();
            BigDecimal newAmount = request.getAmount();

            Category requestCategory = categoryDAO.findById(request.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
            OperationType requestType = requestCategory.getType();

            if (!account.getHousehold().getId().equals(requestCategory.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            Category existingCategory = existingOperation.getCategory();
            OperationType existingType = existingCategory.getType();

            existingOperation.setCategory(requestCategory);
            existingOperation.setDescription(request.getDescription());
            existingOperation.setDateTime(request.getDateTime());
            existingOperation.setAmount(request.getAmount());

            handleAccountAmountChanging(account, existingType, requestType, oldAmount, newAmount);

            accountDAO.save(account);
            operationDAO.update(existingOperation);
        });
    }

    public List<Operation> getFilteredOperations(Long userId, OperationFilterRequest request) {
        return operationDAO.findFilteredOperations(
                userId,
                request.getMinAmount(),
                request.getMaxAmount(),
                request.getDateFrom(),
                request.getDateTo(),
                request.getCategory()
        );
    }
}
