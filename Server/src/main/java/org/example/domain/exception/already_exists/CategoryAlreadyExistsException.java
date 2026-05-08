package org.example.domain.exception.already_exists;

public class CategoryAlreadyExistsException extends AlreadyExistsException {
    public CategoryAlreadyExistsException(String name) {
        super("Category", "name", name);
    }
}
