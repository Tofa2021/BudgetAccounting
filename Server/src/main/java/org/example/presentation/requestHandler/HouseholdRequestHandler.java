package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.HouseholdService;
import org.example.dto.model.HouseholdDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.ModelIdAuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.household.CreateHouseholdRequest;
import org.example.dto.request.household.DeleteHouseholdRequest;
import org.example.dto.request.household.GetHouseholdRequest;
import org.example.dto.request.household.UpdateHouseholdRequest;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class HouseholdRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdService householdService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case CREATE_HOUSEHOLD ->
                    Response.created(dtoMapper.toDTO(householdService.create((CreateHouseholdRequest) request, userId), HouseholdDTO.class));

            case GET_HOUSEHOLD ->
                    Response.success(dtoMapper.toDTO(householdService.get((GetHouseholdRequest) request), HouseholdDTO.class));

            case GET_HOUSEHOLD_AMOUNT ->
                    Response.success(householdService.getAmount((ModelIdAuthorizedRequest) request));

            case UPDATE_HOUSEHOLD -> {
                householdService.update((UpdateHouseholdRequest) request);
                yield Response.noContent();
            }

            case DELETE_HOUSEHOLD -> {
                householdService.delete((DeleteHouseholdRequest) request);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
