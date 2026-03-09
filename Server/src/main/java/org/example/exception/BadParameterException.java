package org.example.exception;

import org.example.dto.Status;

public class BadParameterException extends BusinessException {
    public BadParameterException(String message) {
        super(Status.BAD_REQUEST, message);
    }
}
