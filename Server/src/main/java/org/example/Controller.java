package org.example;

import org.example.dao.BudgetDAO;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.dto.RequestAction;
import org.example.dto.Status;
import org.example.dto.request.*;
import org.example.dto.response.Response;
import org.example.exception.BusinessException;
import org.example.security.JwtProvider;
import org.example.service.BudgetService;
import org.example.service.UserService;

import java.util.NoSuchElementException;

public class Controller {
    private final BudgetService budgetService;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public Controller() {
        BudgetDAO budgetDAO = new BudgetDAO();
        RoleDAO roleDAO = new RoleDAO();
        UserDAO userDAO = new UserDAO();

        jwtProvider = new JwtProvider();
        budgetService = new BudgetService(budgetDAO, userDAO);
        userService = new UserService(jwtProvider, userDAO, roleDAO);
    }

    public Response redirect(Request request) {
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

                            default -> {
                                switch (action) {
                                    case RequestAction.INCREASE_BUDGET_OPERATION ->
                                            budgetService.processIncreaseOperation((IncreaseOperationRequest) request, userId);
                                    case RequestAction.DECREASE_BUDGET_OPERATION ->
                                            budgetService.processDecreaseOperation((DecreaseOperationRequest) request, userId);
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
