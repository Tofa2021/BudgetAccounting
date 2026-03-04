package org.example.dto.request;

import lombok.Getter;
import org.example.dto.IncreaseBudgetCategory;
import org.example.dto.RequestAction;

@Getter
public class IncreaseOperationRequest extends OperationRequest {
    private final IncreaseBudgetCategory category;

    public IncreaseOperationRequest(int amount, IncreaseBudgetCategory category) {
        super(RequestAction.INCREASE_BUDGET_OPERATION, amount);
        this.category = category;
    }
}
