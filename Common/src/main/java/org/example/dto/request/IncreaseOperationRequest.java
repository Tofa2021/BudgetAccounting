package org.example.dto.request;

import lombok.Getter;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.RequestAction;

import java.time.Instant;

@Getter
public class IncreaseOperationRequest extends OperationRequest {
    private final IncreaseOperationCategory category;

    public IncreaseOperationRequest(String token, int amount, Instant dateTime, IncreaseOperationCategory category) {
        super(RequestAction.INCREASE_OPERATION, token, amount, dateTime);
        this.category = category;
    }
}
