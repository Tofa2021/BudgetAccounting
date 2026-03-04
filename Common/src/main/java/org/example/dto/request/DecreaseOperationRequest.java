package org.example.dto.request;

import lombok.Getter;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.RequestAction;

@Getter
public class DecreaseOperationRequest extends OperationRequest {
    private final DecreaseOperationCategory category;

    public DecreaseOperationRequest(int amount, DecreaseOperationCategory category) {
        super(RequestAction.DECREASE_BUDGET_OPERATION, amount);
        this.category = category;
    }
}
