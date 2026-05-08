package org.example.domain.exception;

import org.example.dto.response.Status;

public class BadParameterException extends BusinessException {
    public BadParameterException(String message) {
        super(Status.BAD_REQUEST, message);
    }
}
