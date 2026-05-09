package org.example.domain.exception;

import org.example.response.Status;

public class LastAdminException extends BusinessException {
    public LastAdminException(Long householdId, Long memberId) {
        super(Status.BAD_REQUEST, "Member with id = " + memberId +
                " and role = ADMIN in household with id = " + householdId +
                " cannot be removed or change role because it is last admin");
    }
}
