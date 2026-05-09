package org.example.presentation.requestHandler;

import lombok.RequiredArgsConstructor;
import org.example.application.service.UserService;
import org.example.domain.model.User;
import org.example.dto.UserDTO;
import org.example.request.Request;
import org.example.response.Response;
import org.example.util.DTOMapper;

@RequiredArgsConstructor
public class UserRequestHandler {
    private final DTOMapper dtoMapper;
    private final UserService userService;

    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case LOGOUT -> {
                String refreshToken = request.getParam("refreshToken");

                userService.logout(accessToken, refreshToken);
                yield Response.noContent();
            }

            case GET_USER -> {
                Long id = request.getParam("id");

                User user = userService.get(id);
                yield Response.success(dtoMapper.toDTO(user, UserDTO.class));
            }

            case GET_ME -> {
                User user = userService.getMe(userId);
                yield Response.success(dtoMapper.toDTO(user, UserDTO.class));
            }

            case UPDATE_USER -> {
                String username = request.getParam("username");
                String password = request.getParam("password");

                userService.update(username, password, userId);
                yield Response.noContent();
            }

            case DELETE_USER -> {
                userService.delete(userId);
                yield Response.noContent();
            }

            default -> throw new IllegalArgumentException("Cannot handle request with Action = " + request.action());
        };
    }
}
