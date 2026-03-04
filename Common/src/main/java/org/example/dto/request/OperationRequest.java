package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

@Getter
public class OperationRequest extends Request {
    private final int amount;
    private final Long userId;

    public OperationRequest(RequestAction action, Long userId, int amount) {
        super(action);
        this.amount = amount;
        this.userId = userId;
    }
}
