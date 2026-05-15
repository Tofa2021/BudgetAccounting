package org.example.domain.exception.already_exists;

public class AccountMemberAlreadyExistsException extends AlreadyExistsException {
    public AccountMemberAlreadyExistsException(Long accountId, Long userId) {
        super("User with id = " + userId +
                " is already a member of account with id = " + accountId);
    }
}
