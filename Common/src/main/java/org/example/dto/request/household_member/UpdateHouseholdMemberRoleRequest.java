package org.example.dto.request.household_member;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class UpdateHouseholdMemberRoleRequest extends AuthorizedRequest {
    private final Long memberId;
    private final String newRole;

    public UpdateHouseholdMemberRoleRequest(Long memberId, String newRole) {
        super(RequestAction.UPDATE_HOUSEHOLD_MEMBER_ROLE);
        this.memberId = memberId;
        this.newRole = newRole;
    }
}
