package org.example.dto.request;

import lombok.Getter;

@Getter
public class UpdateRequest<T> extends AuthorizedRequest {
    private final T data;

    public UpdateRequest(RequestAction action, String token, T data) {
        super(action, token);
        this.data = data;
    }
}
