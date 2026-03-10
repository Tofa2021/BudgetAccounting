package org.example.dto.request;

import lombok.Getter;
import org.example.dto.IncreaseOperationCategory;

import java.time.Instant;

@Getter
public class IncreaseOperationFilterRequest extends OperationFilterRequest {
    private final IncreaseOperationCategory category;

    public IncreaseOperationFilterRequest(
            String token,
            Double maxAmount,
            Double minAmount,
            Instant dateFrom,
            Instant dateTo,
            IncreaseOperationCategory category
    ) {
        super(token, maxAmount, minAmount, dateFrom, dateTo);
        this.category = category;
    }
}
