package org.example.dto.request;

import lombok.Getter;

@Getter
public class ModelIdAuthorizedRequest extends AuthorizedRequest {
    private final Long id;

    public ModelIdAuthorizedRequest(RequestAction action, String token, Long id) {
        super(action, token);
        this.id = id;
    }
}
