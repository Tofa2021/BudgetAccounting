package org.example.domain.exception.already_exists;

public class UserAlreadyExistsException extends AlreadyExistsException {
    public UserAlreadyExistsException(String username) {
        super("User", "username", username);
    }
}
