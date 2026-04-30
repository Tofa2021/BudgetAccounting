package org.example.presentation;

import lombok.AllArgsConstructor;
import org.example.application.service.BudgetService;
import org.example.application.service.OperationService;
import org.example.application.service.UserService;
import org.example.domain.exception.BusinessException;
import org.example.domain.model.DTOConvertible;
import org.example.dto.RequestAction;
import org.example.dto.Status;
import org.example.dto.model.DTO;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.*;
import org.example.dto.response.Response;
import org.example.infrastructure.security.TokenProvider;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
public class RequestProcessor {
    private final TokenProvider tokenProvider;
    private final BudgetService budgetService;
    private final UserService userService;
    private final OperationService operationService;

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

        if (isUnauthorizedRequest(request)) {
            return processAuthorizedRequest(action, (AuthorizedRequest) request);
        }

        return processUnauthorizedRequest(action, request);
    }

    private boolean isUnauthorizedRequest(Request request) {
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
            case INCREASE_OPERATION, DECREASE_OPERATION, DELETE_OPERATION, UPDATE_OPERATION ->
                    processVoidOperation(action, request, userId);

            case GET_BUDGET_AMOUNT -> Response.success(budgetService.getAmount(userId));

            case GET_USER_OPERATIONS -> Response.success(toDTOs(operationService.getAllByUserId(userId)));

            case GET_RECENT_OPERATIONS ->
                    Response.success(toDTOs(operationService.getRecentOperations(userId, (IntegerAuthorizedRequest) request)));

            case GET_FILTERED_OPERATIONS ->
                    Response.success(toDTOs(operationService.getFilteredOperations(userId, (OperationFilterRequest) request)));

            default -> throw new NoSuchElementException("Unknown action: " + action);
        };
    }

    private Response processVoidOperation(RequestAction action, AuthorizedRequest request, Long userId) {
        switch (action) {
            case INCREASE_OPERATION ->
                    budgetService.processIncreaseOperation((IncreaseOperationRequest) request, userId);

            case DECREASE_OPERATION ->
                    budgetService.processDecreaseOperation((DecreaseOperationRequest) request, userId);

            case DELETE_OPERATION -> operationService.deleteById((AuthorizedModelIdRequest) request);

            case UPDATE_OPERATION -> operationService.update((UpdateRequest<OperationDTO>) request);

            default -> throw new NoSuchElementException("Unknown void operation: " + action);
        }

        return Response.success(null);
    }

    private <R extends DTO, T extends DTOConvertible<R>> List<R> toDTOs(List<T> elements) {
        if (elements == null) {
            return Collections.emptyList();
        }
        return elements.stream().map(DTOConvertible::toDTO).toList();
    }
}