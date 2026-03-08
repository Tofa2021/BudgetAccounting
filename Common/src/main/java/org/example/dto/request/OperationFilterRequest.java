package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

import java.time.Instant;

@Getter
public class OperationFilterRequest extends AuthorizedRequest {
    private final Integer maxAmount;
    private final Integer minAmount;
    private final Instant dateFrom;
    private final Instant dateTo;

    public OperationFilterRequest(
            String token,
            Integer maxAmount,
            Integer minAmount,
            Instant dateFrom,
            Instant dateTo
    ) {
        super(RequestAction.GET_FILTERED_OPERATIONS, token);
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }
}
