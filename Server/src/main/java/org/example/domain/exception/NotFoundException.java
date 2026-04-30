package org.example.domain.exception;

import org.example.dto.Status;

public class NotFoundException extends ModelException {
    public NotFoundException(String className, String idName, String idValue) {
        super(Status.NOT_FOUND, className, idName, idValue, "not found");
    }
}
