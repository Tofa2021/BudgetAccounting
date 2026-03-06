package org.example.exception;

import org.example.dto.Status;

public class AlreadyExists extends ModelException {
    public AlreadyExists(String className, String idName, String idValue) {
        super(Status.ALREADY_EXISTS, className, idName, idValue, "already exists");
    }
}
