package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

@Getter
public class AuthorizedModelIdRequest extends AuthorizedRequest {
    private final Long modelId;

    public AuthorizedModelIdRequest(Long modelId, RequestAction action, String token) {
        super(action, token);
        this.modelId = modelId;
    }
}
