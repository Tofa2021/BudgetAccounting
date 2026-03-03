package org.example;

import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.Status;
import org.example.dto.request.OperationRequest;
import org.example.dto.request.Request;
import org.example.service.BudgetService;
import org.example.service.Service;

import java.util.NoSuchElementException;

public class Controller {
    private final Service service = new Service();
    private final BudgetService budgetService = new BudgetService();

    public Response redirect(Request request) {
        RequestAction action = request.getAction();
        return new Response(
                Status.OK,
                switch (action) {
                    case RequestAction.GET_BUDGET_AMOUNT -> budgetService.getAmount();
                    default -> {
                        switch (action) {
                            case RequestAction.PLUS_BUDGET_OPERATION ->
                                    budgetService.processPlusOperation((OperationRequest) request);
                            case RequestAction.MINUS_BUDGET_OPERATION ->
                                    budgetService.processMinusOperation((OperationRequest) request);
                            default -> throw new NoSuchElementException();
                        }
                        yield null;
                    }
                });
    }
}
