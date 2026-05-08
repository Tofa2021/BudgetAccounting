package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.UserService;
import org.example.dto.model.UserDTO;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.ModelIdAuthorizedRequest;
import org.example.dto.request.RequestAction;
import org.example.dto.request.user.LogoutRequest;
import org.example.dto.request.user.UpdateUserRequest;
import org.example.dto.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class UserRequestHandler {
    private final DTOMapper dtoMapper;
    private final UserService userService;

    public Response handle(RequestAction action, AuthorizedRequest request, Long userId) {
        return switch (action) {
            case LOGOUT -> {
                userService.logout((LogoutRequest) request);
                yield Response.noContent();
            }

            case GET_USER ->
                    Response.success(dtoMapper.toDTO(userService.get((ModelIdAuthorizedRequest) request), UserDTO.class));

            case GET_ME -> Response.success(dtoMapper.toDTO(userService.getMe(userId), UserDTO.class));

            case UPDATE_USER -> {
                userService.update((UpdateUserRequest) request, userId);
                yield Response.noContent();
            }

            case DELETE_USER -> {
                userService.delete(userId);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + action);
        };
    }
}
