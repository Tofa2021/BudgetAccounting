package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.UserService;
import org.example.dto.request.RequestAction;
import org.example.dto.request.user.AuthRequest;
import org.example.dto.response.Response;

@RequiredArgsConstructor
public class AuthRequestHandler {
    private final UserService userService;

    public Response handle(RequestAction action, AuthRequest request) {
        return switch (action) {
            case SIGN_UP -> Response.success(userService.signUp(request));

            case SIGN_IN -> Response.success(userService.signIn(request));

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
