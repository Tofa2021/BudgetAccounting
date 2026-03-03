package org.example;

import org.example.dto.Request;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.Status;
import org.example.model.MinusBudgetOperation;
import org.example.model.PlusBudgetOperation;
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
                                    budgetService.processPlusOperation((PlusBudgetOperation) request.getParams().get("Body"));
                            case RequestAction.MINUS_BUDGET_OPERATION ->
                                    budgetService.processMinusOperation((MinusBudgetOperation) request.getParams().get("Body"));
                            default -> throw new NoSuchElementException();
                        }
                        yield null;
                    }
                });
    }
}
