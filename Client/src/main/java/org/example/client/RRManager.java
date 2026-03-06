package org.example.client;

import org.example.dto.*;
import org.example.dto.request.*;
import org.example.dto.response.Response;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class RRManager {
    private final ClientConnection clientConnection;
    private final BlockingQueue<Response> responseQueue;
    private String assessToken = "";
    private String refreshToken = "";

    public RRManager() {
        responseQueue = new SynchronousQueue<>();
        clientConnection = new ClientConnection(responseQueue);
    }

    public int getAmount() {
        return (int) putRequest(new AuthorizedRequest(RequestAction.GET_BUDGET_AMOUNT, assessToken)).getBody();
    }

    public boolean signup(String username, String password) {
        Response response = putRequest(new AuthRequest(RequestAction.SIGN_UP, username, password));
        if (response.getStatus() != Status.OK) {
            return false;
        }

        Pair<String, String> tokens = (Pair<String, String>) response.getBody();
        assessToken = tokens.first();
        refreshToken = tokens.second();
        return true;
    }

    public boolean signin(String username, String password) {
        Response response = putRequest(new AuthRequest(RequestAction.SIGN_IN, username, password));
        Status status = response.getStatus();
        if (status != Status.OK) {
            return false;
        }

        Pair<String, String> tokens = (Pair<String, String>) response.getBody();
        assessToken = tokens.first();
        refreshToken = tokens.second();
        return true;
    }

    public void increaseBudget(int amount, IncreaseOperationCategory category) {
        putRequest(new IncreaseOperationRequest(assessToken, amount, category));
    }

    public void decreaseBudget(int amount, DecreaseOperationCategory category) {
        putRequest(new DecreaseOperationRequest(assessToken, amount, category));
    }

    public Response putRequest(Request request) {
        try {
            clientConnection.putRequest(request);
            Response response = responseQueue.take();
            System.out.println(request.getAction() + " " + response.getStatus());
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }
}
