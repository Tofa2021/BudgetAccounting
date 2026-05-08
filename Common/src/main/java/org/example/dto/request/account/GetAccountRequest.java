package org.example.dto.request.account;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetAccountRequest extends AuthorizedRequest {
    private final Long accountId;

    public GetAccountRequest(String token, Long accountId) {
        super(RequestAction.GET_ACCOUNT, token);
        this.accountId = accountId;
    }
}
