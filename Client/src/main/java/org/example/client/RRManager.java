package org.example.client;

import org.example.dto.OperationCategory;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.request.OperationRequest;
import org.example.dto.request.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class RRManager {
    private final ClientConnection clientConnection;
    private final BlockingQueue<Response> responseQueue;

    public RRManager() {
        responseQueue = new SynchronousQueue<>();
        clientConnection = new ClientConnection(responseQueue);
        System.out.println("Last constructor");
    }

    public int getAmount() {
        return (int) putRequest(new Request(RequestAction.GET_BUDGET_AMOUNT)).getBody();
    }

    public void increaseBudget(int amount, OperationCategory category) {
        putRequest(new OperationRequest(RequestAction.PLUS_BUDGET_OPERATION, amount, category));
    }

    public void decreaseBudget(int amount, OperationCategory category) {
        putRequest(new OperationRequest(RequestAction.MINUS_BUDGET_OPERATION, amount, category));
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
