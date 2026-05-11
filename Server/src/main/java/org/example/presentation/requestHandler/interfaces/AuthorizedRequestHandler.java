package org.example.presentation.requestHandler.interfaces;

import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

public abstract class AuthorizedRequestHandler extends BaseRequestHandler {
    public AuthorizedRequestHandler(RequestAction... actions) {
        super(actions);
    }

    public abstract Response handle(Request request, Long userId, String access);
}
