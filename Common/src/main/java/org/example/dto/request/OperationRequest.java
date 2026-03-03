package org.example.dto.request;

import lombok.Getter;
import org.example.dto.OperationCategory;
import org.example.dto.RequestAction;

@Getter
public class OperationRequest extends Request {
    private final int amount;
    private final OperationCategory category;

    public OperationRequest(RequestAction action, int amount, OperationCategory category) {
        super(action);
        this.amount = amount;
        this.category = category;
    }
}
