package org.example.presentation.requestHandler;

import org.example.application.service.HouseholdMemberService;
import org.example.domain.model.HouseholdMember;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

import java.util.List;

public class HouseholdMemberRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdMemberService householdMemberService;

    public HouseholdMemberRequestHandler(DTOMapper dtoMapper, HouseholdMemberService householdMemberService) {
        super(
                RequestAction.CREATE_HOUSEHOLD_MEMBER,
                RequestAction.UPDATE_HOUSEHOLD_MEMBER_ROLE,
                RequestAction.DELETE_HOUSEHOLD_MEMBER,
                RequestAction.GET_HOUSEHOLD_MEMBERS
        );
        this.dtoMapper = dtoMapper;
        this.householdMemberService = householdMemberService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case CREATE_HOUSEHOLD_MEMBER -> {
                Long householdId = request.getParam("householdId");
                Long toCreateUserId = request.getParam("userId");
                String role = request.getParam("role");

                HouseholdMember member = householdMemberService.create(householdId, toCreateUserId, role, userId);
                yield Response.created(dtoMapper.toHouseholdMemberDTO(member));
            }

            case UPDATE_HOUSEHOLD_MEMBER_ROLE -> {
                Long id = request.getParam("id");
                String role = request.getParam("role");

                householdMemberService.updateRole(id, role, userId);
                yield Response.noContent();
            }

            case DELETE_HOUSEHOLD_MEMBER -> {
                Long id = request.getParam("id");

                householdMemberService.delete(id, userId);
                yield Response.noContent();
            }

            case GET_HOUSEHOLD_MEMBERS -> {
                Long householdId = request.getParam("householdId");

                List<HouseholdMember> members = householdMemberService.getAllByHouseholdId(householdId, userId);
                yield Response.success(dtoMapper.toHouseholdMemberDTOs(members));
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
