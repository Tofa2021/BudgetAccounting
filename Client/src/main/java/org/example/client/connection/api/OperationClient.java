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
            Long accountMemberId,
            Long householdId,
            Long categoryId,
            String type,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Instant dateTo,
            Instant dateFrom,
            Integer limit
    ) {
        return sendRequest(
                RequestAction.GET_FILTERED_OPERATIONS,
                Map.of(
                        "householdId", householdId,
                        "accountMemberId", accountMemberId,
                        "categoryId", categoryId,
                        "type", type,
                        "minAmount", minAmount,
                        "maxAmount", maxAmount,
                        "dateTo", dateTo,
                        "dateFrom", dateFrom,
                        "limit", limit
                )
        );
    }

    public Result<List<OperationDTO>> getHouseholdRecentOperations(Long householdId, Instant dateFrom) {
        return sendRequest(
                RequestAction.GET_FILTERED_OPERATIONS,
                Map.of(
                        "householdId", householdId,
                        "dateFrom", dateFrom
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
}
