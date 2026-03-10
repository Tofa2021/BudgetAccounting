package org.example.dto.request;

import lombok.Getter;
import org.example.dto.DecreaseOperationCategory;

import java.time.Instant;

@Getter
public class DecreaseOperationFilterRequest extends OperationFilterRequest {
    private final DecreaseOperationCategory category;

    public DecreaseOperationFilterRequest(
            String token,
            Double maxAmount,
            Double minAmount,
            Instant dateFrom,
            Instant dateTo,
            DecreaseOperationCategory category
    ) {
        super(token, maxAmount, minAmount, dateFrom, dateTo);
        this.category = category;
    }
}
