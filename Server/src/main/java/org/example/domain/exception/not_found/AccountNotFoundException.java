package org.example.domain.exception.not_found;

public class AccountNotFoundException extends NotFoundException {
    public AccountNotFoundException(Long id) {
        super("Account", "Id", String.valueOf(id));
    }
}
