package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.AccountService;
import org.example.domain.model.Account;
import org.example.dto.AccountDTO;
import org.example.enums.Currency;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.request.Request;
import org.example.response.Response;

import java.util.List;

@RequiredArgsConstructor
public class AccountRequestHandler {
    private final DTOMapper dtoMapper;
    private final AccountService accountService;

    public Response handle(Request request, Long userId) {
        return switch (request.action()) {
            case CREATE_ACCOUNT -> {
                Long householdId = request.getParam("householdId");
                String name = request.getParam("name");
                Currency currency = request.getParam("currency");

                Account newAccount = accountService.create(householdId, name, currency, userId);
                yield Response.created(dtoMapper.toDTO(newAccount, AccountDTO.class));
            }

            case GET_ACCOUNT -> {
                Long id = request.getParam("id");

                Account account = accountService.getAccount(id, userId);
                yield Response.success(dtoMapper.toDTO(account, AccountDTO.class));
            }

            case GET_MY_ACCOUNTS_IN_HOUSEHOLD -> {
                Long householdId = request.getParam("householdId");

                List<Account> accounts = accountService.getAccounts(householdId, userId);
                yield Response.success(dtoMapper.toDTOs(accounts, AccountDTO.class));
            }

            case UPDATE_ACCOUNT -> {
                Long id = request.getParam("id");
                String name = request.getParam("name");
                Currency currency = request.getParam("currency");

                accountService.update(id, name, currency);
                yield Response.noContent();
            }

            case DELETE_ACCOUNT -> {
                Long id = request.getParam("id");

                accountService.delete(id);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
