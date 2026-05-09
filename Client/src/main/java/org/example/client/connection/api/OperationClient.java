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

    public Result<List<OperationDTO>> getAllByUserId(Long userId) { // TODO other filter methods
        return sendRequest(
                RequestAction.GET_FILTERED_OPERATIONS,
                Map.of(
                        "userId", userId
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

    public Result<Void> update(Long id, BigDecimal amount, Long categoryId, String description, Instant dateTime) {
        return sendRequest(
                RequestAction.UPDATE_OPERATION,
                Map.of(
                        "id", id,
                        "amount", amount,
                        "categoryId", categoryId,
                        "description", description,
                        "dateTime", dateTime
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
