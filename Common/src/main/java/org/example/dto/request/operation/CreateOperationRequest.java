package org.example.dto.request.operation;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class CreateOperationRequest extends AuthorizedRequest {
    private final Long accountId;
    private final String description;
    private final BigDecimal amount;
    private final Long categoryId;
    private final Instant dateTime;

    public CreateOperationRequest(Long accountId, String description, BigDecimal amount, Long categoryId, Instant dateTime) {
        super(RequestAction.CREATE_OPERATION);
        this.accountId = accountId;
        this.description = description;
        this.amount = amount;
        this.categoryId = categoryId;
        this.dateTime = dateTime;
    }
}
