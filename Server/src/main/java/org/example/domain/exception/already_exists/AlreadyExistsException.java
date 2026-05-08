package org.example.domain.exception.already_exists;

import org.example.domain.exception.ModelException;
import org.example.dto.response.Status;

public class AlreadyExistsException extends ModelException {
    public AlreadyExistsException(String className, String idName, String idValue) {
        super(Status.ALREADY_EXISTS, className, idName, idValue, "already exists");
    }

    public AlreadyExistsException(String message) {
        super(Status.ALREADY_EXISTS, message);
    }
}
