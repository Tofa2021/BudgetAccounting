package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.AccountDTO;
import org.example.dto.request.account.*;
import org.example.enums.Currency;

import java.util.List;

public class AccountClient extends BaseClient {
    public AccountClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<AccountDTO> create(String accountName, Currency currency, Long householdId) {
        return sendAuthorizedRequest(new CreateAccountRequest(accountName, currency, householdId));
    }

    public Result<AccountDTO> get(Long id) {
        return sendAuthorizedRequest(new GetAccountRequest(id));
    }

    public Result<List<AccountDTO>> getMyAccountsInHousehold(Long householdId) {
        return sendAuthorizedRequest(new GetMyAccountsInHouseholdRequest(householdId));
    }

    public Result<Void> update(Long id, String newName, Currency newCurrency) {
        return sendAuthorizedRequest(new UpdateAccountRequest(id, newName, newCurrency));
    }

    public Result<Void> delete(Long id) {
        return sendAuthorizedRequest(new DeleteAccountRequest(id));
    }
}
