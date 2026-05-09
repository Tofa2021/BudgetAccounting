package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.HouseholdDTO;
import org.example.request.RequestAction;

import java.math.BigDecimal;
import java.util.Map;

public class HouseholdClient extends BaseClient {
    public HouseholdClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<BigDecimal> getAmount(Long id) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                Map.of(
                        "id", id
                )
        );
    }

    public Result<HouseholdDTO> create(String name, Map<Long, String> startMembers) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                Map.of(
                        "name", name,
                        "startMembers", startMembers
                )
        );
    }

    public Result<HouseholdDTO> get(Long id) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                Map.of(
                        "id", id
                )
        );
    }

    public Result<Void> update(Long id, String name) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                Map.of(
                        "id", id,
                        "name", name
                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                Map.of(
                        "id", id
                )
        );
    }
}
