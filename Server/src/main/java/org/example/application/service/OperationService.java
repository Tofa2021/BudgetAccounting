package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.*;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.not_found.*;
import org.example.domain.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class OperationService { // TODO check rights
    private final PersistenceManager persistenceManager;
    private final OperationDAO operationDAO;
    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final AccountMemberDAO accountMemberDAO;
    private final UserDAO userDAO;
    private final HouseholdMemberDAO householdMemberDAO;

    public Operation create(
            Long accountId,
            Long categoryId,
            String description,
            BigDecimal amount,
            Instant dateTime,
            Long userId
    ) {
        log.debug("Creating operation with accountId = {} categoryId = {} description = {} amount = {} dateTime = {} userId = {}",
                accountId, categoryId, description, amount, dateTime, userId);

        return persistenceManager.executeTransaction(() -> { // TODO check role
            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));
            Category category = categoryDAO.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            AccountMember accountMember = accountMemberDAO.findByAccountIdAndUserId(accountId, userId)
                    .orElseThrow(() -> new AccountMemberNotFound(accountId, userId));

            if (!account.getHousehold().getId().equals(category.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            Operation operation = new Operation();
            operation.setAccount(account);
            operation.setAccountMember(accountMember);
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

            log.info("Operation created with id = {} accountId = {} categoryId = {} description = {} amount = {} dateTime = {} accountMemberId = {}",
                    operation.getId(), accountId, categoryId, description, amount, dateTime, accountMember.getId());
            return operation;
        });
    }

    public List<Operation> getFilteredOperations( // TODO get by operation type
                                                  Long householdId,
                                                  Long accountMemberId,
                                                  Long categoryId,
                                                  BigDecimal maxAmount,
                                                  BigDecimal minAmount,
                                                  Instant dateFrom,
                                                  Instant dateTo,
                                                  Integer limit
    ) {
        log.debug("Getting filtered operations with householdId = {} accountMemberId = {} categoryId = {} maxAmount = {} " +
                        "minAmount = {} dateFrom = {} dateTo = {} limit = {}",
                householdId, accountMemberId, categoryId, maxAmount, minAmount, dateFrom, dateTo, limit);

        return persistenceManager.executeReadOnly(() -> {
            List<Operation> operations = operationDAO.findWithRelations(OperationFilter.builder()
                    .accountMemberId(accountMemberId)
                    .householdId(householdId)
                    .minAmount(minAmount)
                    .maxAmount(maxAmount)
                    .dateFrom(dateFrom)
                    .dateTo(dateTo)
                    .categoryId(categoryId)
                    .limit(limit)
                    .build());

            log.info("Filtered operations gotten with householdId = {} accountMemberId = {} categoryId = {} maxAmount = {} " +
                            "minAmount = {} dateFrom = {} dateTo = {} limit = {} count = {}",
                    householdId, accountMemberId, categoryId, maxAmount, minAmount, dateFrom, dateTo, limit, operations.size());
            return operations;
        });
    }

    public List<Operation> getUserHouseholdOperations(Long householdId, Long userId) { // TODO check if user can do this
        log.debug("Getting operations with userId = {} accountMemberId = {}", userId, householdId);

        return persistenceManager.executeReadOnly(() -> {
            householdMemberDAO.findByUserIdAndHouseholdId(userId, householdId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            List<AccountMember> accountMembers = accountMemberDAO.getAllByHouseholdIdAndUserId(householdId, userId);

            if (accountMembers.isEmpty()) {
                log.debug("User {} has no account members in household {}", userId, householdId);
                return Collections.emptyList();
            }

            List<Long> accountMemberIds = accountMembers
                    .stream()
                    .map(AccountMember::getId)
                    .toList();

            List<Operation> operations = operationDAO.getAllByAccountMemberIdsWithRelations(accountMemberIds);

            log.info("Operations gotten with householdId = {} userId = {} count = {}", householdId, userId, operations.size());
            return operations;
        });
    }

    public void update(
            Long id,
            BigDecimal newAmount,
            Long newCategoryId,
            String newDescription,
            Instant newDateTime
    ) {
        log.debug("Updating operation with id = {} newAmount = {} newCategoryId = {} newDescription = {} newDateTime = {}",
                id, newAmount, newCategoryId, newDescription, newDateTime);
        // TODO check role and rights
        persistenceManager.executeTransaction(() -> {
            Operation existingOperation = operationDAO.findById(id)
                    .orElseThrow(() -> new OperationNotFoundException(id));

            Long oldCategoryId = existingOperation.getCategory().getId();
            String oldDescription = existingOperation.getDescription();
            Instant oldDateTime = existingOperation.getDateTime();

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

            log.info("Operation updated with id = {} oldAmount = {} newAmount = {} oldCategoryId = {} newCategoryId = {} " +
                            "oldDescription = {} newDescription = {} oldDateTime = {} newDateTime = {}",
                    id, oldAmount, newAmount, oldCategoryId, newCategoryId, oldDescription, newDescription, oldDateTime, newDateTime);
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

    public void delete(Long id) { // TODO check rights
        log.debug("Deleting operation with id = {}", id);

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

            log.info("Operation deleted with id = {}", id);
        });
    }
}
