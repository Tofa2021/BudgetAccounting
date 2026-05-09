package org.example.dto.request.account;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.enums.Currency;

@Getter
public class UpdateAccountRequest extends AuthorizedRequest {
    private final Long accountId;
    private final String name;
    private final Currency currency;

    public UpdateAccountRequest(Long accountId, String name, Currency currency) {
        super(RequestAction.UPDATE_ACCOUNT);
        this.accountId = accountId;
        this.name = name;
        this.currency = currency;
    }
}
