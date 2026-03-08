package org.example.dto.request;

import lombok.Getter;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.RequestAction;

import java.time.Instant;

@Getter
public class DecreaseOperationRequest extends OperationRequest {
    private final DecreaseOperationCategory category;

    public DecreaseOperationRequest(String token, int amount, Instant dateTime, DecreaseOperationCategory category) {
        super(RequestAction.DECREASE_OPERATION, token, amount, dateTime);
        this.category = category;
    }
}
