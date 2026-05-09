package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.AccountMemberDTO;
import org.example.request.RequestAction;

import java.util.Map;

public class AccountMemberClient extends BaseClient {
    public AccountMemberClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<AccountMemberDTO> create(Long householdMemberId, Long accountId, String role) {
        return sendRequest(
                RequestAction.CREATE_ACCOUNT_MEMBER,
                Map.of(
                        "householdMemberId", householdMemberId,
                        "accountId", accountId,
                        "role", role
                )
        );
    }

    public Result<Void> updateRole(Long id, String role) {
        return sendRequest(
                RequestAction.CREATE_ACCOUNT_MEMBER,
                Map.of(
                        "id", id,
                        "role", role
                )
        );
    }

    public Result<Void> delete(Long id) {
        return sendRequest(
                RequestAction.CREATE_ACCOUNT_MEMBER,
                Map.of(
                        "id", id
                )
        );
    }
}
