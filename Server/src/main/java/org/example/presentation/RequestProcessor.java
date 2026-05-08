package org.example.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.BusinessException;
import org.example.dto.request.AuthorizedRequest;
import org.example.dto.request.Request;
import org.example.dto.request.RequestAction;
import org.example.dto.request.user.AuthRequest;
import org.example.dto.response.Response;
import org.example.dto.response.Status;
import org.example.infrastructure.security.TokenProvider;
import org.example.presentation.requestHandler.*;

@Slf4j
@AllArgsConstructor
public class RequestProcessor {
    private final TokenProvider tokenProvider;
    private final AccountMemberRequestHandler accountMemberRequestHandler;
    private final AccountRequestHandler accountRequestHandler;
    private final AuthRequestHandler authRequestHandler;
    private final CategoryRequestHandler categoryRequestHandler;
    private final HouseholdMemberRequestHandler householdMemberRequestHandler;
    private final HouseholdRequestHandler householdRequestHandler;
    private final OperationRequestHandler operationRequestHandler;
    private final UserRequestHandler userRequestHandler;

    public Response process(Request request) {
        try {
            log.info("Received request with Action = {}", request.getAction());
            Response response = processRequest(request);
            log.info("Send response with Status = {}", request.getAction());
            return response;
        } catch (BusinessException e) {
            log.warn(e.getMessage());
            return new Response(e.getStatus(), null);
        } catch (Exception e) {
            log.warn(e.getMessage());
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
        return authRequestHandler.handle(action, (AuthRequest) request);
    }

    private Response processAuthorizedRequest(RequestAction action, AuthorizedRequest request) {
        if (!tokenProvider.isValidateAccessToken(request.getToken())) {
            return new Response(Status.INVALID_TOKEN, null);
        }

        Long userId = tokenProvider.getUserIdFromAccessToken(request.getToken());

        return switch (action) {
            case CREATE_ACCOUNT, GET_ACCOUNT, GET_ACCOUNTS, UPDATE_ACCOUNT, DELETE_ACCOUNT ->
                    accountRequestHandler.handle(action, request, userId);

            case CREATE_ACCOUNT_MEMBER, UPDATE_ACCOUNT_MEMBER_ROLE, DELETE_ACCOUNT_MEMBER ->
                    accountMemberRequestHandler.handle(action, request, userId);

            case CREATE_CATEGORY, GET_CATEGORIES, GET_EXPENSE_CATEGORIES, GET_INCOME_CATEGORIES, UPDATE_CATEGORY,
                 DELETE_CATEGORY -> categoryRequestHandler.handle(action, request, userId);

            case CREATE_HOUSEHOLD_MEMBER, UPDATE_HOUSEHOLD_MEMBER_ROLE, DELETE_HOUSEHOLD_MEMBER ->
                    householdMemberRequestHandler.handle(action, request, userId);

            case CREATE_HOUSEHOLD, GET_HOUSEHOLD, GET_HOUSEHOLD_AMOUNT, UPDATE_HOUSEHOLD, DELETE_HOUSEHOLD ->
                    householdRequestHandler.handle(action, request, userId);

            case CREATE_OPERATION, GET_USER_OPERATIONS, GET_RECENT_OPERATIONS, GET_FILTERED_OPERATIONS,
                 UPDATE_OPERATION, DELETE_OPERATION -> operationRequestHandler.handle(action, request, userId);

            case LOGOUT, GET_ME, GET_USER, UPDATE_USER, DELETE_USER ->
                    userRequestHandler.handle(action, request, userId);

            default -> throw new BadParameterException("Request action = " + action + " is not supported");
        };
    }
}