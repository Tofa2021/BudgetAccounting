package org.example.dto.request;

import lombok.Getter;

@Getter
public class AuthorizedRequest extends Request {
    private final String token;

    public AuthorizedRequest(RequestAction action, String token) {
        super(action);
        this.token = token;
    }
}
