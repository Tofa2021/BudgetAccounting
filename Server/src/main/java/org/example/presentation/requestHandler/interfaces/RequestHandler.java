package org.example.presentation.requestHandler.interfaces;

import org.example.request.RequestAction;

import java.util.Set;

public interface RequestHandler {
    default boolean canHandle(RequestAction action) {
        return getSupportedActions().contains(action);
    }

    Set<RequestAction> getSupportedActions();
}
