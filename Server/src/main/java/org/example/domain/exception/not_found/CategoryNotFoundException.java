package org.example.domain.exception.not_found;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(Long id) {
        super("Category", "Id", String.valueOf(id));
    }
}
