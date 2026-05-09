package org.example.dto.request.account;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteAccountRequest extends AuthorizedRequest {
    private final Long accountId;

    public DeleteAccountRequest(Long accountId) {
        super(RequestAction.DELETE_ACCOUNT);
        this.accountId = accountId;
    }
}
