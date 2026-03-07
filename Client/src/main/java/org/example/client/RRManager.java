package org.example.client;

import org.example.dto.*;
import org.example.dto.model.OperationDTO;
import org.example.dto.request.*;
import org.example.dto.response.Response;

import java.util.List;
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
        return processRequest(new AuthorizedRequest(RequestAction.GET_BUDGET_AMOUNT, assessToken));
    }

    public Result<List<OperationDTO>> getUserOperations() {
        return processRequest(new AuthorizedRequest(RequestAction.GET_USER_OPERATIONS, assessToken));
    }

    public Result<Object> signUp(String username, String password) {
        Result<Pair<String, String>> result = processRequest(new AuthRequest(RequestAction.SIGN_UP, username, password));
        assessToken = result.getData().getFirst();
        refreshToken = result.getData().getSecond();
        return Result.success(null);
    }

    public Result<Object> signIn(String username, String password) {
        Result<Pair<String, String>> result = processRequest(new AuthRequest(RequestAction.SIGN_IN, username, password));
        assessToken = result.getData().getFirst();
        refreshToken = result.getData().getSecond();
        return Result.success(null);
    }

    public Result<Object> increaseBudget(int amount, IncreaseOperationCategory category) {
        return processRequest(new IncreaseOperationRequest(assessToken, amount, category));
    }

    public Result<Object> decreaseBudget(int amount, DecreaseOperationCategory category) {
        return processRequest(new DecreaseOperationRequest(assessToken, amount, category));
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

    private <T> Result<T> processRequest(Request request) {
        Response response = putRequest(request);
        Status status = response.getStatus();
        if (status == Status.OK) {
            System.out.println(response.getBody());
            return Result.success((T) response.getBody());
        }
        return Result.error(status, getErrorMessage(status));
    }
}
