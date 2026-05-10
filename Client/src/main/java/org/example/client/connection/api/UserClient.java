package org.example.client.connection.api;

import org.example.client.Result;
import org.example.client.connection.ServerInteractionManager;
import org.example.dto.UserDTO;
import org.example.request.RequestAction;

import java.util.Map;

public class UserClient extends BaseClient {
    public UserClient(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    public Result<UserDTO> get(Long id) {
        return sendRequest(
                RequestAction.GET_USER,
                Map.of(
                        "id", id
                )
        );
    }

    public Result<UserDTO> getMe() {
        return sendRequest(
                RequestAction.GET_ME,
                Map.of()
        );
    }

    public Result<UserDTO> update(String username, String password) {
        return sendRequest(
                RequestAction.UPDATE_USER,
                Map.of(
                        "username", username,
                        "password", password
                )
        );
    }

    public Result<UserDTO> delete() {
        return sendRequest(
                RequestAction.DELETE_USER,
                Map.of()
        );
    }
}
