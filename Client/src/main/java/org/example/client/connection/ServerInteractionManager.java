package org.example.client.connection;

import org.example.client.Result;
import org.example.request.Request;
import org.example.request.RequestEnvelope;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ServerInteractionManager {
    private final ClientConnection clientConnection;
    private final BlockingQueue<Response> responseQueue;
    private final String accessToken = "";
    private final String refreshToken = "";

    public ServerInteractionManager() {
        responseQueue = new SynchronousQueue<>();
        clientConnection = new ClientConnection(responseQueue);
    }

    public <T> Result<T> processRequest(Request request) {
        Response response = putRequest(request);
        Status status = response.status();
        if (status.isSuccess()) {
            return Result.success(status, (T) response.body());
        }
        return Result.error(status, getErrorMessage(status));
    }

    public <T> Result<T> processAuthorizedRequest(AuthorizedRequest authorizedRequest) {
        authorizedRequest.setToken(accessToken);
        return processRequest(authorizedRequest);
    }

    public Response putRequest(Request request) {
        try {
            clientConnection.putRequest(request);
            Response response = responseQueue.take();
            System.out.println(request.getAction() + " " + response.status());
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }

    private String getErrorMessage(Status status) {
        return switch (status) {
            case Status.NOT_FOUND -> "Не найдено";
            case Status.ALREADY_EXISTS -> "Уже существует";
            case Status.UNKNOWN_SERVER_ERROR -> "Неизвестная ошибка сервера";
            case Status.INVALID_TOKEN -> "Неверный токен";
            case Status.UNAUTHORIZED -> "Неправильный пароль";
            default -> throw new NoSuchElementException();
        };
    }

    public void close() {
        clientConnection.close();
    }
}
