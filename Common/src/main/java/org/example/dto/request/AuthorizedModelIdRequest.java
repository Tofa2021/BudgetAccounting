package org.example.dto.request;

import lombok.Getter;
import org.example.dto.RequestAction;

@Getter
public class AuthorizedModelIdRequest extends AuthorizedRequest {
    private final Long id;

    public AuthorizedModelIdRequest(Long id, RequestAction action, String token) {
        super(action, token);
        this.id = id;
    }
}
