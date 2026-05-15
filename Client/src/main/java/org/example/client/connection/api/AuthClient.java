package org.example.client.connection.api;

import org.example.Pair;
import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.request.RequestAction;

import java.util.Map;

public class AuthClient extends BaseClient {
    private final ServerInteractionManager serverInteractionManager;

    public AuthClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
        this.serverInteractionManager = serverInteractionManager;
    }

    public Result<Pair<String, String>> signIn(String username, String password) {
        return sendRequest(
                RequestAction.SIGN_IN,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
    }

    public Result<Pair<String, String>> signUp(String username, String password) {
        return sendRequest(
                RequestAction.SIGN_UP,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
    }

    public Result<Void> logout(String refreshToken) {
        return sendRequest(
                RequestAction.LOGOUT,
                Map.of(
                        "refreshToken", refreshToken
                )
        );
    }

    public Result<Pair<String, String>> refreshTokens(String refreshToken) {
        return sendRequest(
                RequestAction.REFRESH_TOKENS,
                Map.of(
                        "refreshToken", refreshToken
                )
        );
    }
}
