package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.HouseholdMemberService;
import org.example.domain.model.HouseholdMember;
import org.example.dto.HouseholdMemberDTO;
import org.example.request.Request;
import org.example.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class HouseholdMemberRequestHandler {
    private final DTOMapper dtoMapper;
    private final HouseholdMemberService householdMemberService;

    public Response handle(Request request, Long userId) {
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
