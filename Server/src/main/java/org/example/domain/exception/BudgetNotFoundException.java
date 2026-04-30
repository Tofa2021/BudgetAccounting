package org.example.domain.exception;

public class BudgetNotFoundException extends NotFoundException {
    public BudgetNotFoundException(Long userId) {
        super("Budget", "userId", String.valueOf(userId));
    }
}
