package org.example.presentation.requestHandler;

import org.example.application.service.AccountMemberService;
import org.example.domain.model.AccountMember;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

public class AccountMemberRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final AccountMemberService accountMemberService;

    public AccountMemberRequestHandler(DTOMapper dtoMapper, AccountMemberService accountMemberService) {
        super(
                RequestAction.CREATE_ACCOUNT_MEMBER,
                RequestAction.UPDATE_ACCOUNT_MEMBER_ROLE,
                RequestAction.DELETE_ACCOUNT_MEMBER
        );
        this.dtoMapper = dtoMapper;
        this.accountMemberService = accountMemberService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case CREATE_ACCOUNT_MEMBER -> {
                Long householdMemberId = request.getParam("householdMemberId");
                Long accountId = request.getParam("accountId");
                String role = request.getParam("role");

                AccountMember member = accountMemberService.create(householdMemberId, accountId, role, userId);
                yield Response.created(dtoMapper.toAccountMemberDTO(member));
            }

            case UPDATE_ACCOUNT_MEMBER_ROLE -> {
                Long id = request.getParam("id");
                String role = request.getParam("role");

                accountMemberService.updateRole(id, role, userId);
                yield Response.noContent();
            }

            case DELETE_ACCOUNT_MEMBER -> {
                Long id = request.getParam("id");

                accountMemberService.delete(id, userId);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
