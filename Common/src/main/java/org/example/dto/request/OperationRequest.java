package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

@Getter
public class OperationRequest extends AuthorizedRequest {
    private final int amount;

    public OperationRequest(RequestAction action, String token, int amount) {
        super(action, token);
        this.amount = amount;
    }
}
