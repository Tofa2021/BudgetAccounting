package org.example.dto.request.account;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetMyAccountsInHouseholdRequest extends AuthorizedRequest {
    private final Long householdId;

    public GetMyAccountsInHouseholdRequest(Long householdId) {
        super(RequestAction.GET_MY_ACCOUNTS_IN_HOUSEHOLD);
        this.householdId = householdId;
    }
}
