package org.example.presentation.requestHandler;

import lombok.extern.slf4j.Slf4j;
import org.example.application.service.UserService;
import org.example.domain.model.User;
import org.example.presentation.dtoMapper.DTOMapper;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.response.Response;

@Slf4j
public class UserRequestHandler extends AuthorizedRequestHandler {
    private final DTOMapper dtoMapper;
    private final UserService userService;

    public UserRequestHandler(DTOMapper dtoMapper, UserService userService) {
        super(
                RequestAction.LOGOUT,
                RequestAction.GET_USER,
                RequestAction.GET_ME,
                RequestAction.UPDATE_USER,
                RequestAction.DELETE_USER
        );
        this.dtoMapper = dtoMapper;
        this.userService = userService;
    }

    @Override
    public Response handle(Request request, Long userId, String accessToken) {
        return switch (request.action()) {
            case LOGOUT -> {
                String refreshToken = request.getParam("refreshToken");

                userService.logout(accessToken, refreshToken);
                yield Response.noContent();
            }

            case GET_USER -> {
                String username = request.getParam("username");

                User user = userService.getByUsername(username);
                yield Response.success(dtoMapper.toUserPublicDTO(user));
            }

            case GET_ME -> {
                User user = userService.getMe(userId);
                yield Response.success(dtoMapper.toUserDTO(user));
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
