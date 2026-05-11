package org.example.presentation.requestHandler.interfaces;

import org.example.request.RequestAction;

import java.util.Set;

public abstract class BaseRequestHandler implements RequestHandler {
    private final Set<RequestAction> supportedActions;

    public BaseRequestHandler(RequestAction... actions) {
        supportedActions = Set.of(actions);
    }

    @Override
    public Set<RequestAction> getSupportedActions() {
        return supportedActions;
    }
}
