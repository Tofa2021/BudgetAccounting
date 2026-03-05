package org.example;

import org.example.dao.BudgetDAO;
import org.example.dao.RoleDAO;
import org.example.dao.UserDAO;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.Status;
import org.example.dto.request.*;
import org.example.service.BudgetService;
import org.example.service.UserService;

import java.util.NoSuchElementException;

public class Controller {
    private final BudgetService budgetService;
    private final UserService userService;

    public Controller() {
        BudgetDAO budgetDAO = new BudgetDAO();
        RoleDAO roleDAO = new RoleDAO();
        UserDAO userDAO = new UserDAO();

        budgetService = new BudgetService(budgetDAO, userDAO);
        userService = new UserService(userDAO, roleDAO, budgetDAO);
    }

    public Response redirect(Request request) {
        RequestAction action = request.getAction();
        return new Response(
                Status.OK,
                switch (action) {
                    case RequestAction.GET_BUDGET_AMOUNT ->
                            budgetService.getAmount((Long) ((ParamsRequest) request).getParams().get("userId"));
                    case RequestAction.SIGN_IN -> userService.signin((AuthRequest) request);
                    case RequestAction.SIGN_UP -> userService.signup((AuthRequest) request);
                    default -> {
                        switch (action) {
                            case RequestAction.INCREASE_BUDGET_OPERATION ->
                                    budgetService.processIncreaseOperation((IncreaseOperationRequest) request);
                            case RequestAction.DECREASE_BUDGET_OPERATION ->
                                    budgetService.processDecreaseOperation((DecreaseOperationRequest) request);
                            default -> throw new NoSuchElementException();
                        }
                        yield null;
                    }
                });
    }
}
