package org.example.exception;

public class UserAlreadyExistsException extends AlreadyExists {
    public UserAlreadyExistsException(String username) {
        super("User", "username", username);
    }
}
