package org.example.dto.request;

import lombok.Getter;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.RequestAction;

@Getter
public class IncreaseOperationRequest extends OperationRequest {
    private final IncreaseOperationCategory category;

    public IncreaseOperationRequest(String token, int amount, IncreaseOperationCategory category) {
        super(RequestAction.INCREASE_OPERATION, token, amount);
        this.category = category;
    }
}
