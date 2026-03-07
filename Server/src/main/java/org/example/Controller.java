package org.example;

import lombok.AllArgsConstructor;
import org.example.dto.RequestAction;
import org.example.dto.Status;
import org.example.dto.request.*;
import org.example.dto.response.Response;
import org.example.exception.BusinessException;
import org.example.model.Operation;
import org.example.security.JwtProvider;
import org.example.service.BudgetService;
import org.example.service.OperationService;
import org.example.service.UserService;

import java.util.NoSuchElementException;

@AllArgsConstructor
public class Controller {
    private final BudgetService budgetService;
    private final UserService userService;
    private final OperationService operationService;
    private final JwtProvider jwtProvider;

    public Response process(Request request) {
        RequestAction action = request.getAction();

        try {
            if (request instanceof AuthorizedRequest authorizedRequest) {
                if (!jwtProvider.isValidateAccessToken(authorizedRequest.getToken())) {
                    return new Response(Status.INVALID_TOKEN, null);
                }

                Long userId = Long.parseLong(jwtProvider.getAccessClaims(authorizedRequest.getToken()).getSubject());

                return new Response(
                        Status.OK,
                        switch (action) {
                            case RequestAction.GET_BUDGET_AMOUNT -> budgetService.getAmount(userId);
                            case RequestAction.GET_USER_OPERATIONS -> operationService.getAllByUserId(userId).stream()
                                    .map(Operation::toDTO)
                                    .toList();

                            default -> {
                                switch (action) {
                                    case RequestAction.INCREASE_OPERATION ->
                                            budgetService.processIncreaseOperation((IncreaseOperationRequest) request, userId);
                                    case RequestAction.DECREASE_OPERATION ->
                                            budgetService.processDecreaseOperation((DecreaseOperationRequest) request, userId);
                                    case RequestAction.DELETE_OPERATION ->
                                            operationService.deleteById((AuthorizedModelIdRequest) request);
                                    default -> throw new NoSuchElementException();
                                }
                                yield null;
                            }
                        });
            }

            return new Response(
                    Status.OK,
                    switch (action) {
                        case RequestAction.SIGN_IN -> userService.signin((AuthRequest) request);
                        case RequestAction.SIGN_UP -> userService.signup((AuthRequest) request);
                        default -> throw new NoSuchElementException();
                    }
            );
        } catch (BusinessException e) {
            e.printStackTrace();
            return new Response(e.getStatus(), null);
        }
    }
}
