package org.example.client.connection;

import lombok.Getter;
import lombok.Setter;
import org.example.client.Result;
import org.example.request.Request;
import org.example.request.RequestEnvelope;
import org.example.response.Response;
import org.example.response.Status;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ServerInteractionManager {
    private final ClientConnection clientConnection;
    private final BlockingQueue<Response> responseQueue;
    @Setter
    private String accessToken;
    @Setter
    @Getter
    private String refreshToken;

    public ServerInteractionManager() {
        responseQueue = new SynchronousQueue<>();
        clientConnection = new ClientConnection(responseQueue);
        accessToken = "";
        refreshToken = "";
    }

    public <T> Result<T> processRequest(Request request) {
        Response response = putRequest(request);
        Status status = response.status();
        if (status.isSuccess()) {
            return Result.success(status, (T) response.body());
        }
        return Result.error(status, getErrorMessage(status));
    }

    public Response putRequest(Request request) {
        try {
            RequestEnvelope requestEnvelope = new RequestEnvelope(accessToken, request);
            clientConnection.putRequest(requestEnvelope);
            Response response = responseQueue.take();
            System.out.println(request.action() + " " + response.status());
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
