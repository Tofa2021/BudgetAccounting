package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class GetHouseholdRequest extends AuthorizedRequest {
    private final Long householdId;

    public GetHouseholdRequest(String token, Long householdId) {
        super(RequestAction.GET_HOUSEHOLD, token);
        this.householdId = householdId;
    }
}
