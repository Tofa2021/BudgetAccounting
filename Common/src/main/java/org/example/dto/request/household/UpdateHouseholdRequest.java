package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class UpdateHouseholdRequest extends AuthorizedRequest {
    private final Long householdId;
    private final String name;

    public UpdateHouseholdRequest(Long householdId, String name) {
        super(RequestAction.UPDATE_HOUSEHOLD);
        this.householdId = householdId;
        this.name = name;
    }
}
