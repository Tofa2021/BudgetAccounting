package org.example.domain.exception;

import org.example.dto.Status;

public class ModelException extends BusinessException {
    public ModelException(Status status, String className, String idName, String idValue, String exceptionText) {
        super(status, className + " " + exceptionText + " with " + idName + " = " + idValue);
    }
}
