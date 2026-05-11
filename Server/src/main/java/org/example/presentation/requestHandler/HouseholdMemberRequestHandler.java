package org.example.presentation.requestHandler;

import org.example.application.service.HouseholdMemberService;
import org.example.domain.model.HouseholdMember;
import org.example.dto.HouseholdMemberDTO;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

public class HouseholdMemberRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdMemberService householdMemberService;

    public HouseholdMemberRequestHandler(DTOMapper dtoMapper, HouseholdMemberService householdMemberService) {
        super(
                RequestAction.CREATE_HOUSEHOLD_MEMBER,
                RequestAction.UPDATE_HOUSEHOLD_MEMBER_ROLE,
                RequestAction.DELETE_HOUSEHOLD_MEMBER
        );
        this.dtoMapper = dtoMapper;
        this.householdMemberService = householdMemberService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case CREATE_HOUSEHOLD_MEMBER -> {
                Long householdId = request.getParam("householdId");
                String role = request.getParam("role");

                HouseholdMember member = householdMemberService.create(householdId, role, userId);
                yield Response.created(dtoMapper.toDTO(member, HouseholdMemberDTO.class));
            }

            case UPDATE_HOUSEHOLD_MEMBER_ROLE -> {
                Long id = request.getParam("id");
                String role = request.getParam("role");

                householdMemberService.updateRole(id, role);
                yield Response.noContent();
            }

            case DELETE_HOUSEHOLD_MEMBER -> {
                Long id = request.getParam("id");

                householdMemberService.delete(id);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
