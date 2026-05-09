package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.Pair;
import org.example.application.service.UserService;
import org.example.dto.request.Request;
import org.example.dto.response.Response;

@RequiredArgsConstructor
public class AuthRequestHandler {
    private final UserService userService;

    public Response handle(Request request) {
        return switch (request.getAction()) {
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

            //TODO refresh

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.getAction());
        };
    }
}
