package org.example.client.connection.api;

import org.example.Pair;
import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.UserDTO;
import org.example.request.RequestAction;

import java.util.Map;

public class AuthClient extends BaseClient {
    private final ServerInteractionManager serverInteractionManager;

    public AuthClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
        this.serverInteractionManager = serverInteractionManager;
    }

    public Result<Pair<String, String>> signIn(String username, String password) {
        Result<Pair<String, String>> result = sendRequest(
                RequestAction.SIGN_IN,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
        if (result.isSuccess()) {
            Pair<String, String> tokens = result.getData();
            serverInteractionManager.setAccessToken(tokens.getFirst());
            serverInteractionManager.setRefreshToken(tokens.getSecond());
            return Result.success(result.getStatus(), null);
        }

        return result;
    }

    public Result<Pair<String, String>> signUp(String username, String password) {
        Result<Pair<String, String>> result = sendRequest(
                RequestAction.SIGN_UP,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
        if (result.isSuccess()) {
            Pair<String, String> tokens = result.getData();
            serverInteractionManager.setAccessToken(tokens.getFirst());
            serverInteractionManager.setRefreshToken(tokens.getSecond());
            return Result.success(result.getStatus(), null);
        }

        return result;
    }

    public Result<UserDTO> logout() {
        return sendRequest(
                RequestAction.LOGOUT,
                Map.of(
                        "refreshToken", serverInteractionManager.getRefreshToken()
                )
        );
    }

    // TODO Refresh tokens
}
