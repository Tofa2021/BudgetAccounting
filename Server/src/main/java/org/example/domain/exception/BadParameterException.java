package org.example.domain.exception;

import org.example.response.Status;

public class BadParameterException extends BusinessException {
    public BadParameterException(String message) {
        super(Status.BAD_REQUEST, message);
    }
}
