package org.example.domain.exception;

import org.example.dto.response.Status;

public class LastManagerException extends BusinessException {
    public LastManagerException(Long accountId, Long memberId) {
        super(Status.BAD_REQUEST, "Member with id = " + memberId +
                " and role = MANAGER in account with id = " + accountId +
                " cannot be removed or change role because it is last manager");
    }
}
