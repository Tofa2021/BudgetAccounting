package org.example.dto.request.user;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class UpdateUserRequest extends AuthorizedRequest {
    private final String username;
    private final String password;

    public UpdateUserRequest(String token, String username, String password) {
        super(RequestAction.UPDATE_USER, token);
        this.username = username;
        this.password = password;
    }
}
