package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.AccountMemberService;
import org.example.domain.model.AccountMember;
import org.example.dto.AccountMemberDTO;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.request.Request;
import org.example.response.Response;

@RequiredArgsConstructor
public class AccountMemberRequestHandler {
    private final DTOMapper dtoMapper;
    private final AccountMemberService accountMemberService;

    public Response handle(Request request, Long userId) {
        return switch (request.action()) {
            case CREATE_ACCOUNT_MEMBER -> {
                Long householdMemberId = request.getParam("householdMemberId");
                Long accountId = request.getParam("accountId");
                String role = request.getParam("role");

                AccountMember member = accountMemberService.create(householdMemberId, accountId, role);
                yield Response.created(dtoMapper.toDTO(member, AccountMemberDTO.class));
            }

            case UPDATE_ACCOUNT_MEMBER_ROLE -> {
                Long memberId = request.getParam("memberId");
                String role = request.getParam("role");

                accountMemberService.updateRole(memberId, role);
                yield Response.noContent();
            }

            case DELETE_ACCOUNT_MEMBER -> {
                Long id = request.getParam("id");

                accountMemberService.delete(id);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
