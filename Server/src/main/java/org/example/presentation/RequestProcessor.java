package org.example.presentation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.exception.BusinessException;
import org.example.infrastructure.security.TokenProvider;
import org.example.presentation.requestHandler.*;
import org.example.presentation.requestHandler.interfaces.AuthorizedRequestHandler;
import org.example.presentation.requestHandler.interfaces.RequestHandler;
import org.example.presentation.requestHandler.interfaces.UnauthorizedRequestHandler;
import org.example.request.Request;
import org.example.request.RequestAction;
import org.example.request.RequestEnvelope;
import org.example.response.Response;
import org.example.response.Status;

import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class RequestProcessor { // TODO if request have token user cannot use SIGN_IN and SIGN_UP
    private final TokenProvider tokenProvider;
    private final AccountMemberRequestHandler accountMemberRequestHandler;
    private final AccountRequestHandler accountRequestHandler;
    private final AuthRequestHandler authRequestHandler;
    private final CategoryRequestHandler categoryRequestHandler;
    private final HouseholdMemberRequestHandler householdMemberRequestHandler;
    private final HouseholdRequestHandler householdRequestHandler;
    private final OperationRequestHandler operationRequestHandler;
    private final UserRequestHandler userRequestHandler;

    private final Set<AuthorizedRequestHandler> authorizedRequestHandlers = Set.of(
            accountMemberRequestHandler,
            accountRequestHandler,
            categoryRequestHandler,
            householdMemberRequestHandler,
            householdRequestHandler,
            operationRequestHandler,
            userRequestHandler
    );

    private final Set<UnauthorizedRequestHandler> unauthorizedRequestHandlers = Set.of(
            authRequestHandler
    );

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
        UnauthorizedRequestHandler requestHandler = getSupportedActionRequestHandler(request.action(), unauthorizedRequestHandlers);
        return requestHandler.handle(request);
    }

    private Response processAuthorizedRequest(Request request, String accessToken) {
        if (!tokenProvider.isValidateAccessToken(accessToken)) {
            return new Response(Status.INVALID_TOKEN, null);
        }

        Long userId = tokenProvider.getUserIdFromAccessToken(accessToken);
        log.info("Current userId access token from token = {}", userId);

        AuthorizedRequestHandler requestHandler = getSupportedActionRequestHandler(request.action(), authorizedRequestHandlers);
        return requestHandler.handle(request, userId, accessToken);
    }

    private <T extends RequestHandler> T getSupportedActionRequestHandler(RequestAction action, Set<T> handlers) {
        return handlers
                .stream()
                .filter(t -> t.canHandle(action))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported request action: " + action));
    }
}
