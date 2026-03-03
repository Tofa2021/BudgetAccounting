package org.example;

import org.example.dto.Request;
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
        String action = request.getAction();
        return new Response(
                Status.OK,
                switch (action) {
                    case "GetBudgetAmount" -> budgetService.getAmount();
                    default -> {
                        switch (action) {
                            case "BudgetOperation" ->
                                    budgetService.processOperation((Operation) request.getParams().get("Body"));
                            default -> throw new NoSuchElementException();
                        }
                        yield null;
                    }
                });
    }
}
