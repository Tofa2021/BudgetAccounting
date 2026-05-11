package org.example.application.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.PersistenceManager;
import org.example.domain.dao.*;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.not_found.AccountNotFoundException;
import org.example.domain.exception.not_found.CategoryNotFoundException;
import org.example.domain.exception.not_found.OperationNotFoundException;
import org.example.domain.exception.not_found.UserNotFoundException;
import org.example.domain.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
public class OperationService { // TODO check rights and TODO logging
    private final PersistenceManager persistenceManager;
    private final OperationDAO operationDAO;
    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final UserDAO userDAO;

    public Operation create(
            Long accountId,
            Long categoryId,
            String description,
            BigDecimal amount,
            Instant dateTime,
            Long userId
    ) {
        return persistenceManager.executeTransaction(() -> {
            User user = userDAO.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));
            Category category = categoryDAO.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));

            if (!account.getHousehold().getId().equals(category.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            Operation operation = new Operation();
            operation.setAccount(account);
            operation.setUser(user);
            operation.setDescription(description);
            operation.setCategory(category);
            operation.setAmount(amount);
            operation.setDateTime(dateTime);
            operationDAO.save(operation);

            if (category.getType() == OperationType.INCOME) {
                account.increase(amount);
            } else {
                account.decrease(amount);
            }
            accountDAO.save(account);

            return operation;
        });
    }

    public List<Operation> getFilteredOperations( // TODO get by operation type
                                                  Long householdId,
                                                  Long userId,
                                                  Long categoryId,
                                                  BigDecimal maxAmount,
                                                  BigDecimal minAmount,
                                                  Instant dateFrom,
                                                  Instant dateTo,
                                                  Integer limit
    ) {
        return persistenceManager.executeReadOnly(() ->
                operationDAO.findWithRelations(OperationFilter.builder()
                        .userId(userId)
                        .householdId(householdId)
                        .minAmount(minAmount)
                        .maxAmount(maxAmount)
                        .dateFrom(dateFrom)
                        .dateTo(dateTo)
                        .categoryId(categoryId)
                        .limit(limit)
                        .build()
                ));
    }

    public List<Operation> getUserHouseholdOperations(Long householdId, Long userId) {
        return persistenceManager.executeReadOnly(() ->
                operationDAO.findWithRelations(OperationFilter.builder()
                        .householdId(householdId)
                        .userId(userId)
                        .build()));
    }

    public void update(
            Long id,
            BigDecimal newAmount,
            Long newCategoryId,
            String newDescription,
            Instant newDateTime
    ) {
        persistenceManager.executeTransaction(() -> {
            Operation existingOperation = operationDAO.findById(id)
                    .orElseThrow(() -> new OperationNotFoundException(id));
            Account account = existingOperation.getAccount();

            BigDecimal oldAmount = existingOperation.getAmount();

            Category requestCategory = categoryDAO.findById(newCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(newCategoryId));
            OperationType requestType = requestCategory.getType();

            if (!account.getHousehold().getId().equals(requestCategory.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            Category existingCategory = existingOperation.getCategory();
            OperationType existingType = existingCategory.getType();

            existingOperation.setCategory(requestCategory);
            existingOperation.setDescription(newDescription);
            existingOperation.setDateTime(newDateTime);
            existingOperation.setAmount(newAmount);

            handleAccountAmountChanging(account, existingType, requestType, oldAmount, newAmount);

            accountDAO.save(account);
            operationDAO.update(existingOperation);
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

    public void delete(Long id) {
        persistenceManager.executeTransaction(() -> {
            Operation operation = operationDAO.findById(id)
                    .orElseThrow(() -> new OperationNotFoundException(id));

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
}
