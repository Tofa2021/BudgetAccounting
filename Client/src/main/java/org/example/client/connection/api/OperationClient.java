package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.OperationDTO;
import org.example.request.RequestAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class OperationClient extends BaseClient {
    public OperationClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<OperationDTO> create(
            Long accountId,
            String description,
            BigDecimal amount,
            Long categoryId,
            Instant dateTime
    ) {
        return sendRequest(
                RequestAction.CREATE_OPERATION,
                Map.of(
                        "accountId", accountId,
                        "categoryId", categoryId,
                        "description", description,
                        "amount", amount,
                        "dateTime", dateTime
                )
        );
    }

    public Result<List<OperationDTO>> getMyHouseholdOperations(Long householdId) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_OPERATIONS,
                Map.of("householdId", householdId)
        );
    }

    public Result<List<OperationDTO>> getFilteredOperations(
            Long householdId,
            Long accountId,
            Long creatorUserId,
            Long categoryId,
            String operationType,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Instant dateTo,
            Instant dateFrom,
            Integer limit,
            String sortBy,
            String sortDirection
    ) {
        return sendRequest(
                RequestAction.GET_FILTERED_OPERATIONS,
                Map.ofEntries(
                        Map.entry("householdId", householdId),
                        Map.entry("accountId", accountId),
                        Map.entry("creatorUserId", creatorUserId),
                        Map.entry("categoryId", categoryId),
                        Map.entry("operationType", operationType),
                        Map.entry("minAmount", minAmount),
                        Map.entry("maxAmount", maxAmount),
                        Map.entry("dateTo", dateTo),
                        Map.entry("dateFrom", dateFrom),
                        Map.entry("limit", limit),
                        Map.entry("sortBy", sortBy),
                        Map.entry("sortDirection", sortDirection)
                )
        );
    }

    public Result<List<OperationDTO>> getExpenses(Long householdId, Instant dateFrom) {
        return sendRequest(
                RequestAction.GET_EXPENSES,
                Map.of(
                        "householdId", householdId,
                        "dateFrom", dateFrom
                )
        );
    }

    public Result<List<OperationDTO>> getAllExpenses(Long householdId) {
        return sendRequest(
                RequestAction.GET_EXPENSES,
                Map.of(
                        "householdId", householdId
                )
        );
    }

    public Result<List<OperationDTO>> getHouseholdOperations(Long householdId) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_OPERATIONS,
                Map.of(
                        "householdId", householdId
                )
        );
    }

    public Result<List<OperationDTO>> getAccountOperations(Long accountId) {
        return sendRequest(
                RequestAction.GET_ACCOUNT_OPERATIONS,
                Map.of(
                        "accountId", accountId
                )
        );
    }

    public Result<List<OperationDTO>> getMyOperations(Long householdID) {
        return sendRequest(
                RequestAction.GET_MY_OPERATIONS,
                Map.of(
                        "householdID", householdID
                )
        );
    }

    public Result<Void> update(OperationDTO operationDTO) {
        return sendRequest(
                RequestAction.UPDATE_OPERATION,
                Map.of(
                        "id", operationDTO.getId(),
                        "amount", operationDTO.getAmount(),
                        "categoryId", operationDTO.getCategoryId(),
                        "description", operationDTO.getDescription(),
                        "dateTime", operationDTO.getDateTime()
                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.DELETE_OPERATION,
                Map.of(
                        "id", id
                )
        );
    }

    public Result<List<OperationDTO>> getIncomes(Long householdId, Instant dateFrom) {
        return sendRequest(
                RequestAction.GET_INCOMES,
                Map.of(
                        "householdId", householdId,
                        "dateFrom", dateFrom
                )
        );
    }
}
