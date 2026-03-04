package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

@Getter
public class AuthRequest extends Request {
    private final String username;
    private final String password;

    public AuthRequest(RequestAction action, String username, String password) {
        super(action);
        this.username = username;
        this.password = password;
    }
}
