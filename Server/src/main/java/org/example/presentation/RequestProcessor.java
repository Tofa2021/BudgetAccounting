package org.example.presentation;

import lombok.AllArgsConstructor;
import org.example.application.service.*;
import org.example.domain.exception.BusinessException;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.*;
import org.example.dto.request.operation.DeleteOperationRequest;
import org.example.dto.request.operation.OperationFilterRequest;
import org.example.dto.request.operation.OperationRequest;
import org.example.dto.request.user.AuthRequest;
import org.example.dto.response.Response;
import org.example.dto.response.Status;
import org.example.infrastructure.security.TokenProvider;
import org.example.util.DTOMapper;

import java.util.NoSuchElementException;

@AllArgsConstructor
public class RequestProcessor {
    private final TokenProvider tokenProvider;
    private final DTOMapper dtoMapper;
    private final AccountMemberService accountMemberService;
    private final AccountService accountService;
    private final CategoryService categoryService;
    private final HouseholdMemberService householdMemberService;
    private final HouseholdService householdService;
    private final OperationService operationService;
    private final UserService userService;

    public Response process(Request request) {
        try {
            return processRequest(request);
        } catch (BusinessException e) {
            return new Response(e.getStatus(), null);
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(Status.SERVER_ERROR, null);
        }
    }

    private Response processRequest(Request request) {
        RequestAction action = request.getAction();

        if (isAuthorizedRequest(request)) {
            return processAuthorizedRequest(action, (AuthorizedRequest) request);
        }

        return processUnauthorizedRequest(action, request);
    }

    private boolean isAuthorizedRequest(Request request) {
        return request instanceof AuthorizedRequest;
    }

    private Response processUnauthorizedRequest(RequestAction action, Request request) {
        Object result = switch (action) {
            case SIGN_IN -> userService.signIn((AuthRequest) request);

            case SIGN_UP -> userService.signUp((AuthRequest) request);

            default -> throw new IllegalStateException("Unexpected unauthorized action: " + action);
        };

        return Response.success(result);
    }

    private Response processAuthorizedRequest(RequestAction action, AuthorizedRequest request) {
        if (!tokenProvider.isValidateAccessToken(request.getToken())) {
            return new Response(Status.INVALID_TOKEN, null);
        }

        Long userId = tokenProvider.getUserIdFromAccessToken(request.getToken());

        return switch (action) {
            case CREATE_OPERATION, DELETE_OPERATION, UPDATE_OPERATION -> processVoidOperation(action, request, userId);

            case GET_HOUSEHOLD_AMOUNT ->
                    Response.success(householdService.getAmount((ModelIdAuthorizedRequest) request));

            case GET_USER_OPERATIONS ->
                    Response.success(dtoMapper.toDTOs(operationService.getAllByUserId(userId), OperationDTO.class));

            case GET_RECENT_OPERATIONS ->
                    Response.success(dtoMapper.toDTOs(operationService.getRecentOperations(userId, (IntegerAuthorizedRequest) request), OperationDTO.class));

            case GET_FILTERED_OPERATIONS ->
                    Response.success(dtoMapper.toDTOs(operationService.getFilteredOperations(userId, (OperationFilterRequest) request), OperationDTO.class));

            default -> throw new NoSuchElementException("Unknown action: " + action);
        };
    }

    private Response processVoidOperation(RequestAction action, AuthorizedRequest request, Long userId) {
        switch (action) {
            case CREATE_OPERATION -> operationService.create((OperationRequest) request, userId);

            case DELETE_OPERATION -> operationService.deleteById((DeleteOperationRequest) request, userId);

            case UPDATE_OPERATION -> operationService.update((UpdateRequest<OperationDTO>) request);

            default -> throw new NoSuchElementException("Unknown void operation: " + action);
        }

        return Response.success(null);
    }
}