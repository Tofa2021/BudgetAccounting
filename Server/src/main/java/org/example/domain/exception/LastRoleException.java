package org.example.domain.exception;

import org.example.domain.model.AccountMemberRole;
import org.example.domain.model.HouseholdMemberRole;
import org.example.response.Status;

public class LastRoleException extends BusinessException {
    public LastRoleException(Long memberId, Long householdId, HouseholdMemberRole role) {
        super(Status.BAD_REQUEST, "Member with id = " + memberId +
                " and role = " + role + " in household with id = " + householdId +
                " cannot be removed or change last member with the role");
    }

    public LastRoleException(Long householdId, HouseholdMemberRole role) {
        super(Status.BAD_REQUEST, "Cannot remove last " + role + " in household id = " + householdId);
    }

    public LastRoleException(Long memberId, Long accountId, AccountMemberRole role) {
        super(Status.BAD_REQUEST, "Member with id = " + memberId +
                " and role = " + role + " in account with id = " + accountId +
                " cannot be removed or change last member with the role");
    }

    public LastRoleException(String message) {
        super(Status.BAD_REQUEST, message);
    }
}
