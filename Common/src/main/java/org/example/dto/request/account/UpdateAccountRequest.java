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

    public UpdateAccountRequest(RequestAction action, String token, Long accountId, String name, Currency currency) {
        super(action, token);
        this.accountId = accountId;
        this.name = name;
        this.currency = currency;
    }
}
