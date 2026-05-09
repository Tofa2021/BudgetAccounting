package org.example.dto.request.household;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class CreateHouseholdMemberRequest extends AuthorizedRequest {
    private final Long householdId;
    private final Long userId;
    private final String role;

    public CreateHouseholdMemberRequest(Long householdId, Long userId, String role) {
        super(RequestAction.CREATE_HOUSEHOLD_MEMBER);
        this.householdId = householdId;
        this.userId = userId;
        this.role = role;
    }
}
