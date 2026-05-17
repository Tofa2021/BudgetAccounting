package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.HouseholdDTO;
import org.example.enums.Currency;
import org.example.request.RequestAction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class HouseholdClient extends BaseClient {
    public HouseholdClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<Map<Currency, BigDecimal>> getAmount(Long id) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD_AMOUNT,
                Map.of(
                        "id", id
                )
        );
    }

    public Result<HouseholdDTO> create(String name, Map<Long, String> startMembers) {
        return sendRequest(
                RequestAction.CREATE_HOUSEHOLD,
                Map.of(
                        "name", name,
                        "startMembers", startMembers
                )
        );
    }

    public Result<HouseholdDTO> get(Long id) {
        return sendRequest(
                RequestAction.GET_HOUSEHOLD,
                Map.of(
                        "id", id
                )
        );
    }

    public Result<List<HouseholdDTO>> getMyHouseholds() {
        return sendRequest(
                RequestAction.GET_MY_HOUSEHOLDS,
                Map.of()
        );
    }

    public Result<Void> update(Long id, String name) {
        return sendRequest(
                RequestAction.UPDATE_HOUSEHOLD,
                Map.of(
                        "id", id,
                        "name", name
                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.DELETE_HOUSEHOLD,
                Map.of(
                        "id", id
                )
        );
    }
}
