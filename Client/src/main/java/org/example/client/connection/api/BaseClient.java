package org.example.client.connection.api;

import lombok.RequiredArgsConstructor;
import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.request.Request;
import org.example.request.RequestAction;

import java.util.Map;

@RequiredArgsConstructor
public abstract class BaseClient {
    private final ServerInteractionManager serverInteractionManager;

    protected <T> Result<T> sendRequest(RequestAction action, Map<String, Object> params) {
        return serverInteractionManager.processRequest(new Request(action, params));
    }
}
