package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.AccountDTO;
import org.example.enums.Currency;
import org.example.request.RequestAction;

import java.util.List;
import java.util.Map;

public class AccountClient extends BaseClient {
    public AccountClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<AccountDTO> create(String accountName, Currency currency, Long householdId) {
        return sendRequest(
                RequestAction.CREATE_ACCOUNT,
                Map.of(
                        "accountName", accountName,
                        "currency", currency,
                        "householdId", householdId
                )
        );
    }

    public Result<AccountDTO> get(Long id) {
        return sendRequest(
                RequestAction.GET_ACCOUNT,
                Map.of("id", id)
        );
    }

    public Result<List<AccountDTO>> getMyAccountsInHousehold(Long householdId) {
        return sendRequest(
                RequestAction.GET_MY_ACCOUNTS_IN_HOUSEHOLD,
                Map.of("householdId", householdId)
        );
    }

    public Result<Void> update(Long id, String newName, Currency newCurrency) {
        return sendRequest(
                RequestAction.UPDATE_ACCOUNT,
                Map.of(
                        "id", id,
                        "newName", newName,
                        "currency", newCurrency
                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.DELETE_ACCOUNT,
                Map.of("id", id)
        );
    }
}
