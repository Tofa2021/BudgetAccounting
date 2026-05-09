package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.HouseholdDTO;
import org.example.dto.request.ModelIdAuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.household.CreateHouseholdRequest;
import org.example.dto.request.household.DeleteHouseholdRequest;
import org.example.dto.request.household.GetHouseholdRequest;
import org.example.dto.request.household.UpdateHouseholdRequest;

import java.math.BigDecimal;
import java.util.Map;

public class HouseholdClient extends BaseClient {
    public HouseholdClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<BigDecimal> getAmount(Long id) {
        return sendAuthorizedRequest(new ModelIdAuthorizedRequest(RequestAction.GET_HOUSEHOLD_AMOUNT, id));
    }

    public Result<HouseholdDTO> create(String name, Map<Long, String> startMembers) {
        return sendAuthorizedRequest(new CreateHouseholdRequest(name, startMembers));
    }

    public Result<HouseholdDTO> get(Long id) {
        return sendAuthorizedRequest(new GetHouseholdRequest(id));
    }

    public Result<Void> update(Long id, String name) {
        return sendAuthorizedRequest(new UpdateHouseholdRequest(id, name));
    }

    public Result<Void> delete(Long id) {
        return sendAuthorizedRequest(new DeleteHouseholdRequest(id));
    }
}
