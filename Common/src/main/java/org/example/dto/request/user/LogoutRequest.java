package org.example.dto.request.user;

import lombok.Getter;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.RequestAction;

@Getter
public class LogoutRequest extends AuthorizedRequest {
    private final String refreshToken;

    public LogoutRequest(String accessToken, String refreshToken) {
        super(RequestAction.LOGOUT, accessToken);
        this.refreshToken = refreshToken;
    }
}
