package org.example.domain.exception.already_exists;

public class UserAlreadyExistsExceptionException extends AlreadyExistsException {
    public UserAlreadyExistsExceptionException(String username) {
        super("User", "username", username);
    }
}
