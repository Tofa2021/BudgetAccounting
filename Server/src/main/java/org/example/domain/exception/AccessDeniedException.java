package org.example.domain.exception;

import org.example.dto.response.Status;

public class AccessDeniedException extends BusinessException {
    public AccessDeniedException(String message) {
        super(Status.FORBITTEN, message);
    }
}
