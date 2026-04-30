package org.example.domain.exception;

public class UserAlreadyExistsException extends AlreadyExists {
    public UserAlreadyExistsException(String username) {
        super("User", "username", username);
    }
}
