package org.example.dto.request.user;

import lombok.Getter;
import org.example.dto.request.Request;
import org.example.dto.request.RequestAction;

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
