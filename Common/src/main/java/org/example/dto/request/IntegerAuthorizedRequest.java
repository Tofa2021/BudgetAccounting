package org.example.dto.request;

import lombok.Getter;

@Getter
public class IntegerAuthorizedRequest extends AuthorizedRequest {
    private final Integer integer;

    public IntegerAuthorizedRequest(RequestAction action, String token, Integer integer) {
        super(action, token);
        this.integer = integer;
    }
}
