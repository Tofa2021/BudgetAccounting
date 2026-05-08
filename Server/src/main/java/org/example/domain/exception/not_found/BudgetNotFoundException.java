package org.example.domain.exception.not_found;

public class BudgetNotFoundException extends NotFoundException {
    public BudgetNotFoundException(String idName, Long id) {
        super("Budget", idName, String.valueOf(id));
    }

    public BudgetNotFoundException(Long id) {
        super("Budget", "Id", String.valueOf(id));
    }
}
