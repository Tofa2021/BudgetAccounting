package org.example.dto.request.account;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.enums.Currency;

@Getter
public class CreateAccountRequest extends AuthorizedRequest {
    private final String name;
    private final Currency currency;
    private final Long householdId;

    public CreateAccountRequest(String token, String name, Currency currency, Long householdId) {
        super(RequestAction.CREATE_ACCOUNT, token);
        this.name = name;
        this.currency = currency;
        this.householdId = householdId;
    }
}
