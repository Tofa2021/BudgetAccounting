package org.example.presentation.requestHandler;

import org.example.Pair;
import org.example.application.service.UserService;
import org.example.presentation.requestHandler.interfaces.UnauthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

public class AuthRequestHandler extends UnauthorizedRequestHandler {
    private final UserService userService;

    public AuthRequestHandler(UserService userService) {
        super(
                RequestAction.SIGN_UP,
                RequestAction.SIGN_IN
        );
        this.userService = userService;
    }

    @Override
    public Response handle(Request request) {
        return switch (request.action()) {
            case SIGN_UP -> {
                String username = request.getParam("username");
                String password = request.getParam("password");

                Pair<String, String> tokens = userService.signUp(username, password);
                yield Response.success(tokens);
            }

            case SIGN_IN -> {
                String username = request.getParam("username");
                String password = request.getParam("password");

                Pair<String, String> tokens = userService.signIn(username, password);
                yield Response.success(tokens);
            }

            case REFRESH_TOKENS -> {
                String refreshToken = request.getParam("refreshToken");

                Pair<String, String> tokens = userService.refreshTokens(refreshToken);
                yield Response.success(tokens);
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
