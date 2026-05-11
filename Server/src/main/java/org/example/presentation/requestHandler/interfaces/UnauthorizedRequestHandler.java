package org.example.presentation.requestHandler.interfaces;

import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

public abstract class UnauthorizedRequestHandler extends BaseRequestHandler {
    public UnauthorizedRequestHandler(RequestAction... actions) {
        super(actions);
    }

    public abstract Response handle(Request request);
}
