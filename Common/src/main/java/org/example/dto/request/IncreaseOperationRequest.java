package org.example.dto.request;

import lombok.Getter;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.RequestAction;

@Getter
public class IncreaseOperationRequest extends OperationRequest {
    private final IncreaseOperationCategory category;

    public IncreaseOperationRequest(int amount, Long userId, IncreaseOperationCategory category) {
        super(RequestAction.INCREASE_BUDGET_OPERATION, userId, amount);
        this.category = category;
    }
}
