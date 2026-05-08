package org.example.domain.exception.not_found;

public class OperationNotFoundException extends NotFoundException {
    public OperationNotFoundException(Long id) {
        super("Operation", "id", String.valueOf(id));
    }
}
