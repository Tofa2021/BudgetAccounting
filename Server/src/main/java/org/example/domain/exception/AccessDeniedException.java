package org.example.domain.exception;

import org.example.response.Status;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(Status.FORBIDDEN, message);
    }
}
