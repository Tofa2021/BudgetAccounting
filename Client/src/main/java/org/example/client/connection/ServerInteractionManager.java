package org.example.client.connection;

import org.example.client.Result;
import org.example.client.SessionContext;
import org.example.request.Request;
import org.example.request.RequestEnvelope;
import org.example.response.Response;
import org.example.response.Status;

import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ServerInteractionManager {
    private final ServerConnection serverConnection;
    private final BlockingQueue<Response> responseQueue;
    private final SessionContext sessionContext;

    public ServerInteractionManager(SessionContext sessionContext) {
        this.sessionContext = sessionContext;
        responseQueue = new SynchronousQueue<>();
        serverConnection = new ServerConnection(responseQueue);
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
            RequestEnvelope requestEnvelope = new RequestEnvelope(sessionContext.getAccessToken(), request);
            serverConnection.putRequest(requestEnvelope);
            Response response = responseQueue.take();
            System.out.println(request.action() + " " + response.status());
            return response;
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted", e);
        }
    }

    private String getErrorMessage(Status status) {
        return switch (status) {
            case NOT_FOUND -> "Не найдено";
            case ALREADY_EXISTS -> "Уже существует";
            case UNKNOWN_SERVER_ERROR -> "Неизвестная ошибка сервера";
            case INVALID_TOKEN -> "Неверный токен";
            case UNAUTHORIZED -> "Неправильный пароль";
            case BAD_REQUEST -> "Неверный запрос";
            case CONNECTION_ERROR -> "Проблема с подключением к серверу";
            default -> throw new NoSuchElementException();
        };
    }

    public void close() {
        serverConnection.close();
    }
}
