package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.AccountService;
import org.example.dto.model.AccountDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.account.*;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class AccountRequestHandler {
    private final DTOMapper dtoMapper;
    private final AccountService accountService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case CREATE_ACCOUNT ->
                    Response.created(dtoMapper.toDTO(accountService.create((CreateAccountRequest) request, userId), AccountDTO.class));

            case GET_ACCOUNT ->
                    Response.success(dtoMapper.toDTO(accountService.getAccount((GetAccountRequest) request, userId), AccountDTO.class));

            case GET_ACCOUNTS ->
                    Response.success(dtoMapper.toDTOs(accountService.getAccounts((GetAccountsRequest) request, userId), AccountDTO.class));

            case UPDATE_ACCOUNT -> {
                accountService.update((UpdateAccountRequest) request);
                yield Response.noContent();
            }

            case DELETE_ACCOUNT -> {
                accountService.delete((DeleteAccountRequest) request);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
