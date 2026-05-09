package org.example.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizedRequest extends Request {
    private String token;

    public AuthorizedRequest(RequestAction action) {
        super(action);
    }
}