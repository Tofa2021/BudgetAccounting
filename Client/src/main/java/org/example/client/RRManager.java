package org.example.client;

import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseBudgetCategory;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.request.DecreaseOperationRequest;
import org.example.dto.request.IncreaseOperationRequest;
import org.example.dto.request.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class RRManager {
    private final ClientConnection clientConnection;
    private final BlockingQueue<Response> responseQueue;

    public RRManager() {
        responseQueue = new SynchronousQueue<>();
        clientConnection = new ClientConnection(responseQueue);
    }

    public int getAmount() {
        return (int) putRequest(new Request(RequestAction.GET_BUDGET_AMOUNT)).getBody();
    }

    public void increaseBudget(int amount, IncreaseBudgetCategory category) {
        putRequest(new IncreaseOperationRequest(amount, category));
    }

    public void decreaseBudget(int amount, DecreaseOperationCategory category) {
        putRequest(new DecreaseOperationRequest(amount, category));
    }

    public Response putRequest(Request request) {
        try {
            clientConnection.putRequest(request);
            Response response = responseQueue.take();
            System.out.println(response.getStatus());
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }
}
