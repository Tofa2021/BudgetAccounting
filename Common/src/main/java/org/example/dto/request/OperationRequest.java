package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

import java.time.Instant;

@Getter
public class OperationRequest extends AuthorizedRequest {
    private final int amount;
    private final Instant dateTime;

    public OperationRequest(RequestAction action, String token, int amount, Instant dateTime) {
        super(action, token);
        this.amount = amount;
        this.dateTime = dateTime;
    }
}
