package org.example;

import org.example.dto.Request;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.Status;
import org.example.model.Operation;
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
                            case RequestAction.BUDGET_OPERATION ->
                                    budgetService.processOperation((Operation) request.getParams().get("Body"));
                            default -> throw new NoSuchElementException();
                        }
                        yield null;
                    }
                });
    }
}
