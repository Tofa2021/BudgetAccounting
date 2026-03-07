package org.example.client;

import org.example.dto.*;
import org.example.dto.request.*;
import org.example.dto.response.Response;

import java.util.NoSuchElementException;
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

    public Result<Integer> getAmount() {
        Response response = putRequest(new AuthorizedRequest(RequestAction.GET_BUDGET_AMOUNT, assessToken));
        Status status = response.getStatus();
        if (status == Status.OK) {
            return Result.success((Integer) response.getBody());
        }
        return Result.error(status, getErrorMessage(status));
    }

    public Result<Object> signUp(String username, String password) {
        Response response = putRequest(new AuthRequest(RequestAction.SIGN_UP, username, password));
        Status status = response.getStatus();
        if (status == Status.OK) {
            Pair<String, String> tokens = (Pair<String, String>) response.getBody();
            assessToken = tokens.getFirst();
            refreshToken = tokens.getSecond();
            return Result.success(null);
        }
        return Result.error(status, getErrorMessage(status));
    }

    public Result<Object> signIn(String username, String password) {
        Response response = putRequest(new AuthRequest(RequestAction.SIGN_IN, username, password));
        Status status = response.getStatus();
        if (status == Status.OK) {
            Pair<String, String> tokens = (Pair<String, String>) response.getBody();
            assessToken = tokens.getFirst();
            refreshToken = tokens.getSecond();
            return Result.success(null);
        }
        return Result.error(status, getErrorMessage(status));
    }

    public Result<Object> increaseBudget(int amount, IncreaseOperationCategory category) {
        Response response = putRequest(new IncreaseOperationRequest(assessToken, amount, category));
        Status status = response.getStatus();
        if (status == Status.OK) {
            return Result.success(null);
        }
        return Result.error(status, getErrorMessage(status));
    }

    public Result<Object> decreaseBudget(int amount, DecreaseOperationCategory category) {
        Response response = putRequest(new DecreaseOperationRequest(assessToken, amount, category));
        Status status = response.getStatus();
        if (status == Status.OK) {
            return Result.success(null);
        }
        return Result.error(status, getErrorMessage(status));
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

    private String getErrorMessage(Status status) {
        return switch (status) {
            case Status.NOT_FOUND -> "Не найдено";
            case Status.ALREADY_EXISTS -> "Уже существует";
            case Status.SERVER_ERROR -> "Неизвестная ошибка сервера";
            default -> throw new NoSuchElementException();
        };
    }
}
