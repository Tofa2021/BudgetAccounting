package org.example.domain.exception.forbidden;

import org.example.domain.exception.BusinessException;
import org.example.response.Status;

public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(Status.FORBIDDEN, message);
    }
}
