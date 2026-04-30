package org.example.domain.exception;

public class OperationNotFoundException extends NotFoundException {
    public OperationNotFoundException(Long id) {
        super("Operation", "id", String.valueOf(id));
    }
}
