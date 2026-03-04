package org.example.client;

import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.RequestAction;
import org.example.dto.Response;
import org.example.dto.request.*;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class RRManager {
    private final ClientConnection clientConnection;
    private final BlockingQueue<Response> responseQueue;

    public RRManager() {
        responseQueue = new SynchronousQueue<>();
        clientConnection = new ClientConnection(responseQueue);
    }

    public int getAmount(Long userId) {
        return (int) putRequest(new ParamsRequest(RequestAction.GET_BUDGET_AMOUNT, Map.of("userId", userId))).getBody();
    }

    public Long signup(String username, String password) {
        return (Long) putRequest(new AuthRequest(RequestAction.SIGN_UP, username, password)).getBody();
    }

    public Long signin(String username, String password) {
        return (Long) putRequest(new AuthRequest(RequestAction.SIGN_IN, username, password)).getBody();
    }

    public void increaseBudget(int amount, Long userId, IncreaseOperationCategory category) {
        putRequest(new IncreaseOperationRequest(amount, userId, category));
    }

    public void decreaseBudget(int amount, Long userId, DecreaseOperationCategory category) {
        putRequest(new DecreaseOperationRequest(amount, userId, category));
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
