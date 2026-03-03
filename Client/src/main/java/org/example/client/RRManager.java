package org.example.client;

import org.example.dto.Request;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.model.Operation;

import java.util.Map;
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
        return (int) putRequest(new Request(RequestAction.GET_BUDGET_AMOUNT, Map.of())).getBody();
    }

    public void increaseBudget(int amount) {
        putRequest(new Request(RequestAction.BUDGET_OPERATION, Map.of("Body", new Operation(amount, "Plus"))));
    }

    public void decreaseBudget(int amount) {
        putRequest(new Request(RequestAction.BUDGET_OPERATION, Map.of("Body", new Operation(amount, "Minus"))));
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
