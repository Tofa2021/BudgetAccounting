package org.example.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.dao.*;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.forbidden.ForbiddenException;
import org.example.domain.exception.forbidden.RoleRequiredException;
import org.example.domain.exception.not_found.*;
import org.example.domain.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class OperationService {
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "dateTime", "amount", "description", "id"
    );
    private final PersistenceManager persistenceManager;
    private final HouseholdPermissionChecker householdPermissionChecker;
    private final AccountPermissionChecker accountPermissionChecker;
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

        return persistenceManager.executeTransaction(() -> {
            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));
            Category category = categoryDAO.findById(categoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(categoryId));
            AccountMember accountMember = accountMemberDAO.findByAccountIdAndUserId(accountId, userId)
                    .orElseThrow(() -> new AccountMemberNotFoundException(accountId, userId));

            AccountMemberRole role = accountMember.getRole();
            if (role != AccountMemberRole.MANAGER && role != AccountMemberRole.WRITER) {
                throw new RoleRequiredException(role, AccountMemberRole.MANAGER, AccountMemberRole.WRITER);
            }

            if (!account.getHousehold().getId().equals(category.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            Operation operation = new Operation();
            operation.setCreatedByUserId(userId);
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

    public List<Operation> getFilteredOperations(
            Long householdId,
            Long accountId,
            Long creatorUserId,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Instant dateFrom,
            Instant dateTo,
            Long categoryId,
            OperationType type,
            Integer limit,
            String sortBy,
            String sortDirection,
            Long userId
    ) {
        log.debug("Getting filtered operations with householdId = {} accountId = {} creatorUserId = {} categoryId = {} operationType = {} maxAmount = {} " +
                        "minAmount = {} dateFrom = {} dateTo = {} limit = {} sortBy = {} sortDirection = {}",
                householdId, accountId, creatorUserId, categoryId, type, maxAmount, minAmount, dateFrom, dateTo, limit, sortBy, sortDirection);

        return persistenceManager.executeReadOnly(() -> {
            HouseholdMemberRole householdRole = householdMemberDAO.findRoleByHouseholdIdAndUserId(householdId, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            List<Long> accountIds = getAccessibleAccountIds(householdId, userId, householdRole);

            if (accountIds.isEmpty()) {
                log.debug("No accessible accounts for user {} in household {}", userId, householdId);
                return Collections.emptyList();
            }

            if (accountId != null && !accountIds.contains(accountId)) {
                throw new ForbiddenException("No access to account with id: " + accountId);
            }

            if (categoryId != null) {
                Category category = categoryDAO.findById(categoryId)
                        .orElseThrow(() -> new CategoryNotFoundException(categoryId));

                if (!category.getHousehold().getId().equals(householdId)) {
                    throw new BadParameterException("Category does not belong to this household");
                }
            }

            if (creatorUserId != null && householdRole == HouseholdMemberRole.MEMBER) {
                if (!creatorUserId.equals(userId)) {
                    throw new ForbiddenException("Member can only filter operations by themselves");
                }
            }

            List<Long> targetAccountIds;
            if (accountId != null) {
                targetAccountIds = List.of(accountId);
            } else {
                targetAccountIds = accountIds;
            }

            List<Long> targetUserIds = null;
            if (creatorUserId != null) {
                targetUserIds = List.of(creatorUserId);
            } else if (householdRole == HouseholdMemberRole.MEMBER) {
                targetUserIds = List.of(userId);
            }

            if (minAmount != null && maxAmount != null && minAmount.compareTo(maxAmount) > 0) {
                throw new BadParameterException("minAmount cannot be greater than maxAmount");
            }

            if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
                throw new BadParameterException("dateFrom cannot be after dateTo");
            }

            if (limit != null && limit <= 0) {
                throw new BadParameterException("limit must be positive");
            }

            String finalSortBy = sortBy != null && !sortBy.isBlank() ? sortBy : "dateTime";
            validateSortBy(finalSortBy);
            String finalSortDirection = sortDirection != null && !sortDirection.isBlank() ? sortDirection : "DESC";

            List<Operation> operations = operationDAO.findWithRelations(OperationFilter.builder()
                    .accountIds(targetAccountIds)
                    .userIds(targetUserIds)
                    .categoryId(categoryId)
                    .type(type)
                    .minAmount(minAmount)
                    .maxAmount(maxAmount)
                    .dateFrom(dateFrom)
                    .dateTo(dateTo)
                    .limit(limit)
                    .sortBy(finalSortBy)
                    .sortDirection(finalSortDirection)
                    .build());

            log.debug("Filtered operations gotten with count={} householdId={} userId={}",
                    operations.size(), householdId, userId);
            return operations;
        });
    }

    private List<Long> getAccessibleAccountIds(Long householdId, Long userId, HouseholdMemberRole role) {
        if (role == HouseholdMemberRole.OWNER || role == HouseholdMemberRole.MANAGER) {
            return accountDAO.getAllIdsByHouseholdId(householdId);
        } else {
            return accountMemberDAO.getIdsByUserIdAndHouseholdId(userId, householdId);
        }
    }

    private void validateSortBy(String sortBy) {
        if (sortBy != null && !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            throw new BadParameterException("Invalid sort field: " + sortBy +
                    ". Allowed fields: " + ALLOWED_SORT_FIELDS);
        }
    }

    public List<Operation> getAccountOperations(Long accountId, Long userId) {
        log.debug("Getting operations for accountId = {} userId = {}", accountId, userId);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            Account account = accountDAO.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException(accountId));

            Long householdId = account.getHousehold().getId();
            HouseholdMemberRole role = householdMemberDAO.findRoleByHouseholdIdAndUserId(householdId, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            if (role == HouseholdMemberRole.MEMBER) {
                accountPermissionChecker.checkMembership(accountId, userId);
            }

            List<Operation> operations = operationDAO.getAllByAccountIdWithRelations(accountId);

            log.debug("Operations gotten with accountId = {} userId = {} count = {}", accountId, userId, operations.size());
            return operations;
        });
    }

    public List<Operation> getHouseholdOperations(Long householdId, Long userId) {
        log.debug("Getting operations for householdId={}, userId={}", householdId, userId);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            householdPermissionChecker.checkRole(householdId, userId,
                    HouseholdMemberRole.OWNER, HouseholdMemberRole.MANAGER);

            return operationDAO.getAllByHouseholdIdWithRelations(householdId);
        });
    }

    public List<Operation> getMyOperations(Long householdId, Long userId) {
        log.debug("Getting my operations for householdId={}, userId={}", householdId, userId);

        return persistenceManager.executeReadOnlyTransaction(() -> {
            householdPermissionChecker.checkMembership(householdId, userId);

            return operationDAO.getAllByUserIdAndHouseholdIdWithRelations(userId, householdId);
        });
    }

    private boolean canManageOperation(Operation operation, HouseholdMemberRole householdRole, AccountMemberRole accountRole, Long userId) {
        if (householdRole == HouseholdMemberRole.OWNER ||
                householdRole == HouseholdMemberRole.MANAGER ||
                accountRole == AccountMemberRole.MANAGER) {
            return true;
        }

        if (accountRole == AccountMemberRole.WRITER) {
            return Objects.equals(operation.getCreatedByUserId(), userId);
        }

        return false;
    }

    public void update(
            Long id,
            BigDecimal newAmount,
            Long newCategoryId,
            String newDescription,
            Instant newDateTime,
            Long userId
    ) {
        log.debug("Updating operation with id = {} newAmount = {} newCategoryId = {} newDescription = {} newDateTime = {}",
                id, newAmount, newCategoryId, newDescription, newDateTime);

        persistenceManager.executeTransaction(() -> {
            Operation existingOperation = operationDAO.findById(id)
                    .orElseThrow(() -> new OperationNotFoundException(id));

            Long householdId = existingOperation.getAccount().getHousehold().getId();
            HouseholdMemberRole householdRole = householdMemberDAO.findRoleByHouseholdIdAndUserId(householdId, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            Long accountId = existingOperation.getAccount().getId();
            AccountMemberRole accountRole = accountMemberDAO.findRoleByAccountIdAndUserId(accountId, userId)
                    .orElseThrow(() -> new AccountMemberNotFoundException(accountId, userId));

            if (!canManageOperation(existingOperation, householdRole, accountRole, userId)) {
                throw new ForbiddenException("No permission to update this operation");
            }

            updateOperation(existingOperation, newAmount, newCategoryId, newDescription, newDateTime);
        });
    }

    private void updateOperation(
            Operation existingOperation,
            BigDecimal newAmount,
            Long newCategoryId,
            String newDescription,
            Instant newDateTime
    ) {
        Long oldCategoryId = existingOperation.getCategory().getId();
        String oldDescription = existingOperation.getDescription();
        Instant oldDateTime = existingOperation.getDateTime();
        Account account = existingOperation.getAccount();
        BigDecimal oldAmount = existingOperation.getAmount();

        if (newCategoryId != null) {
            Category newCategory = categoryDAO.findById(newCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(newCategoryId));

            if (!account.getHousehold().getId().equals(newCategory.getHousehold().getId())) {
                throw new BadParameterException("Account and category must be in the same household");
            }

            existingOperation.setCategory(newCategory);
        }

        if (newAmount != null) {
            if (newAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new BadParameterException("Amount must be positive");
            }
            existingOperation.setAmount(newAmount);
        }

        if (newDescription != null) {
            existingOperation.setDescription(newDescription);
        }

        if (newDateTime != null) {
            existingOperation.setDateTime(newDateTime);
        }

        OperationType oldType = existingOperation.getCategory().getType();
        OperationType newType = existingOperation.getCategory().getType();

        if (newAmount != null || newCategoryId != null) {
            BigDecimal actualNewAmount = newAmount != null ? newAmount : oldAmount;
            handleAccountAmountChanging(account, oldType, newType, oldAmount, actualNewAmount);
        }

        operationDAO.update(existingOperation);

        log.info("Operation updated with id = {} oldAmount = {} newAmount = {} oldCategoryId = {} newCategoryId = {} " +
                        "oldDescription = {} newDescription = {} oldDateTime = {} newDateTime = {}",
                existingOperation.getId(), oldAmount, newAmount, oldCategoryId, newCategoryId, oldDescription, newDescription, oldDateTime, newDateTime);
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

        accountDAO.save(account);
    }

    public void delete(Long id, Long userId) {
        log.debug("Deleting operation with id = {}", id);

        persistenceManager.executeTransaction(() -> {
            Operation operation = operationDAO.findById(id)
                    .orElseThrow(() -> new OperationNotFoundException(id));

            Long householdId = operation.getAccount().getHousehold().getId();
            HouseholdMemberRole householdRole = householdMemberDAO.findRoleByHouseholdIdAndUserId(householdId, userId)
                    .orElseThrow(() -> new HouseholdMemberNotFoundException(userId, householdId));

            Long accountId = operation.getAccount().getId();
            AccountMemberRole accountRole = accountMemberDAO.findRoleByAccountIdAndUserId(accountId, userId)
                    .orElseThrow(() -> new AccountMemberNotFoundException(accountId, userId));

            if (!canManageOperation(operation, householdRole, accountRole, userId)) {
                throw new ForbiddenException("No permission to delete this operation");
            }


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
