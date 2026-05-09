package org.example.presentation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.exception.BadParameterException;
import org.example.domain.exception.BusinessException;
import org.example.infrastructure.security.TokenProvider;
import org.example.presentation.requestHandler.*;
import org.example.request.Request;
import org.example.request.RequestEnvelope;
import org.example.response.Response;
import org.example.response.Status;

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

    public Response process(RequestEnvelope requestEnvelope) {
        try {
            log.info("Received request with Action = {}", requestEnvelope.request().action());
            Response response = processRequest(requestEnvelope);
            log.info("Send response with Status = {}", response.status());
            return response;
        } catch (BusinessException e) {
            log.warn(e.getMessage());
            return new Response(e.getStatus(), e.getMessage());
        } catch (Exception e) {
            log.warn(e.getMessage());
            return new Response(Status.UNKNOWN_SERVER_ERROR, e.getMessage());
        }
    }

    private Response processRequest(RequestEnvelope requestEnvelope) {
        Request request = requestEnvelope.request();
        if (requestEnvelope.isAuthenticated()) {
            return processAuthorizedRequest(request, requestEnvelope.accessToken());
        }

        return processUnauthorizedRequest(request);
    }

    private Response processUnauthorizedRequest(Request request) {
        return authRequestHandler.handle(request);
    }

    private Response processAuthorizedRequest(Request request, String accessToken) {
        if (!tokenProvider.isValidateAccessToken(accessToken)) {
            return new Response(Status.INVALID_TOKEN, null);
        }

        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);

        return switch (request.action()) {
            case CREATE_ACCOUNT, GET_ACCOUNT, GET_MY_ACCOUNTS_IN_HOUSEHOLD, UPDATE_ACCOUNT, DELETE_ACCOUNT ->
                    accountRequestHandler.handle(request, userId);

            case CREATE_ACCOUNT_MEMBER, UPDATE_ACCOUNT_MEMBER_ROLE, DELETE_ACCOUNT_MEMBER ->
                    accountMemberRequestHandler.handle(request, userId);

            case CREATE_CATEGORY, GET_CATEGORIES, GET_EXPENSE_CATEGORIES, GET_INCOME_CATEGORIES, UPDATE_CATEGORY,
                 DELETE_CATEGORY -> categoryRequestHandler.handle(request, userId);

            case CREATE_HOUSEHOLD_MEMBER, UPDATE_HOUSEHOLD_MEMBER_ROLE, DELETE_HOUSEHOLD_MEMBER ->
                    householdMemberRequestHandler.handle(request, userId);

            case CREATE_HOUSEHOLD, GET_HOUSEHOLD, GET_HOUSEHOLD_AMOUNT, UPDATE_HOUSEHOLD, DELETE_HOUSEHOLD ->
                    householdRequestHandler.handle(request, userId);

            case CREATE_OPERATION, GET_FILTERED_OPERATIONS,
                 UPDATE_OPERATION, DELETE_OPERATION -> operationRequestHandler.handle(request, userId);

            case LOGOUT, GET_ME, GET_USER, UPDATE_USER, DELETE_USER ->
                    userRequestHandler.handle(request, userId, accessToken);

            default -> throw new BadParameterException("Request action = " + request.action() + " is not supported");
        };
    }
}
