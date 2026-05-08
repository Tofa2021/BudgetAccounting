package org.example.dto.request.operation;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class DeleteOperationRequest extends AuthorizedRequest {
    private final Long budgetId;
    private final Long id;

    public DeleteOperationRequest(RequestAction action, String token, Long budgetId, Long id) {
        super(action, token);
        this.budgetId = budgetId;
        this.id = id;
    }
}
