package org.example.dto.request.account;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetAccountsRequest extends AuthorizedRequest {
    private final Long householdId;

    public GetAccountsRequest(String token, Long householdId) {
        super(RequestAction.GET_ACCOUNTS, token);
        this.householdId = householdId;
    }
}
