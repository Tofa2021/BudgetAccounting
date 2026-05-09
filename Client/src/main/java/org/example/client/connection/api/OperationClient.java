package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.household.DeleteHouseholdRequest;
import org.example.dto.request.household.UpdateHouseholdRequest;
import org.example.dto.request.operation.CreateOperationRequest;
import org.example.dto.request.operation.OperationFilterRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
        return sendAuthorizedRequest(new CreateOperationRequest(accountId, description, amount, categoryId, dateTime));
    }

    public Result<List<OperationDTO>> getAllByUserId(Long userId) {
        return sendAuthorizedRequest(new OperationFilterRequest(
                null,
                userId,
                null,
                null,
                null,
                null,
                null,
                null
        ));
    }

    public Result<List<OperationDTO>> getHouseholdRecentOperations(Long id, Instant dateFrom) {
        return sendAuthorizedRequest(new OperationFilterRequest(
                id,
                null,
                null,
                null,
                null,
                dateFrom,
                null,
                null
        ));
    }

    public Result<Void> update(Long id, String newName) {
        return sendAuthorizedRequest(new UpdateHouseholdRequest(id, newName));
    }

    public Result<Void> delete(Long id) {
        return sendAuthorizedRequest(new DeleteHouseholdRequest(id));
    }
}
