package org.example.dto.request;

import lombok.Getter;

@Getter
public class ModelIdAuthorizedRequest extends AuthorizedRequest {
    private final Long id;

    public ModelIdAuthorizedRequest(RequestAction action, Long id) {
        super(action);
        this.id = id;
    }
}
