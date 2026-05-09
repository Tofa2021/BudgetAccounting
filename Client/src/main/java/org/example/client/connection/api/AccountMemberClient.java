package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.model.AccountMemberDTO;
import org.example.dto.request.account_member.CreateAccountMemberRequest;
import org.example.dto.request.account_member.DeleteAccountMemberRequest;
import org.example.dto.request.account_member.UpdateAccountMemberRoleRequest;

public class AccountMemberClient extends BaseClient {
    public AccountMemberClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<AccountMemberDTO> create(Long householdMemberId, Long accountId, String role) {
        return sendAuthorizedRequest(new CreateAccountMemberRequest(householdMemberId, accountId, role));
    }

    public Result<Void> updateRole(Long memberId, String newRole) {
        return sendAuthorizedRequest(new UpdateAccountMemberRoleRequest(memberId, newRole));
    }

    public Result<Void> delete(Long memberId) {
        return sendAuthorizedRequest(new DeleteAccountMemberRequest(memberId));
    }
}
