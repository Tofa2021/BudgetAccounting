package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

@Getter
public class OperationRequest extends Request {
    private final int amount;

    public OperationRequest(RequestAction action, int amount) {
        super(action);
        this.amount = amount;
    }
}
