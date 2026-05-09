package org.example.dto.request.operation;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class OperationFilterRequest extends AuthorizedRequest {
    private final Long householdId;
    private final Long userId;
    private final Long categoryId;
    private final BigDecimal maxAmount;
    private final BigDecimal minAmount;
    private final Instant dateFrom;
    private final Instant dateTo;
    private final Integer limit;

    public OperationFilterRequest(
            Long householdId,
            Long userId,
            Long categoryId,
            BigDecimal maxAmount,
            BigDecimal minAmount,
            Instant dateFrom,
            Instant dateTo, Integer limit
    ) {
        super(RequestAction.GET_FILTERED_OPERATIONS);
        this.householdId = householdId;
        this.userId = userId;
        this.categoryId = categoryId;
        this.maxAmount = maxAmount;
        this.minAmount = minAmount;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.limit = limit;
    }
}
