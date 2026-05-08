package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.AccountMemberService;
import org.example.dto.model.AccountMemberDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.account_member.CreateAccountMemberRequest;
import org.example.dto.request.account_member.DeleteAccountMemberRequest;
import org.example.dto.request.account_member.UpdateAccountMemberRoleRequest;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class AccountMemberRequestHandler {
    private final DTOMapper dtoMapper;
    private final AccountMemberService accountMemberService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case CREATE_ACCOUNT_MEMBER ->
                    Response.created(dtoMapper.toDTO(accountMemberService.create((CreateAccountMemberRequest) request), AccountMemberDTO.class));

            case UPDATE_ACCOUNT_MEMBER_ROLE -> {
                accountMemberService.updateRole((UpdateAccountMemberRoleRequest) request);
                yield Response.noContent();
            }

            case DELETE_ACCOUNT_MEMBER -> {
                accountMemberService.delete((DeleteAccountMemberRequest) request);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
