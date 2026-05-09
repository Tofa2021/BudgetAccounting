package org.example.client.connection.api;

import lombok.RequiredArgsConstructor;
import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.Request;

@RequiredArgsConstructor
public abstract class BaseClient {
    private final ServerInteractionManager serverInteractionManager;

    protected <T> Result<T> sendRequest(Request request) {
        return serverInteractionManager.processRequest(request);
    }

    protected <T> Result<T> sendAuthorizedRequest(AuthorizedRequest request) {
        return serverInteractionManager.processAuthorizedRequest(request);
    }
}
