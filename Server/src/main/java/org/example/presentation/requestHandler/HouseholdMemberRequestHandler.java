package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.HouseholdMemberService;
import org.example.dto.model.HouseholdMemberDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.household.CreateHouseholdMemberRequest;
import org.example.dto.request.household.DeleteHouseholdMemberRequest;
import org.example.dto.request.household_member.UpdateHouseholdMemberRoleRequest;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class HouseholdMemberRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdMemberService householdMemberService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case CREATE_HOUSEHOLD_MEMBER ->
                    Response.created(dtoMapper.toDTO(householdMemberService.create((CreateHouseholdMemberRequest) request), HouseholdMemberDTO.class));

            case UPDATE_HOUSEHOLD_MEMBER_ROLE -> {
                householdMemberService.updateRole((UpdateHouseholdMemberRoleRequest) request);
                yield Response.noContent();
            }

            case DELETE_HOUSEHOLD_MEMBER -> {
                householdMemberService.delete((DeleteHouseholdMemberRequest) request);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
