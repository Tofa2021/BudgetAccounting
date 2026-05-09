package org.example.dto.request.operation;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class OperationFilterRequest extends AuthorizedRequest {
    private final Long categoryId;
    private final BigDecimal maxAmount;
    private final BigDecimal minAmount;
    private final Instant dateFrom;
    private final Instant dateTo;

    public OperationFilterRequest(
            String token,
            Long categoryId,
            BigDecimal maxAmount,
            BigDecimal minAmount,
            Instant dateFrom,
            Instant dateTo
    ) {
        super(RequestAction.GET_FILTERED_OPERATIONS, token);
        this.categoryId = categoryId;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }
}
