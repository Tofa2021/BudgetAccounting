package org.example.dto.request.operation;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
public class UpdateOperationRequest extends AuthorizedRequest {
    private final Long id;
    private final String description;
    private final BigDecimal amount;
    private final Instant dateTime;
    private final Long categoryId;

    public UpdateOperationRequest(String token, Long id, String description, BigDecimal amount, Instant dateTime, Long categoryId) {
        super(RequestAction.UPDATE_OPERATION, token);
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.dateTime = dateTime;
        this.categoryId = categoryId;
    }
}
