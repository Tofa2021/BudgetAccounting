package org.example.domain.exception.not_found;

import org.example.domain.exception.ModelException;
import org.example.response.Status;

public class NotFoundException extends ModelException {
    public NotFoundException(String className, String idName, String idValue) {
        super(Status.NOT_FOUND, className, idName, idValue, "not found");
    }

    public NotFoundException(String message) {
        super(Status.NOT_FOUND, message);
    }
}
