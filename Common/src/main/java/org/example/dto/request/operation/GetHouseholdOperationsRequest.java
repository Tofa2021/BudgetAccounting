package org.example.dto.request.operation;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetHouseholdOperationsRequest extends AuthorizedRequest {
    private final Long householdId;

    public GetHouseholdOperationsRequest(Long householdId) {
        super(RequestAction.GET_HOUSEHOLD_OPERATIONS);
        this.householdId = householdId;
    }
}
