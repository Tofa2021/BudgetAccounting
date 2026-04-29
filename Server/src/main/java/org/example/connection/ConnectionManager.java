package org.example.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class ConnectionManager {
    private final RequestProcessor requestProcessor;

    public ConnectionManager(RequestProcessor requestProcessor) {
        this.requestProcessor = requestProcessor;
    }

    public void start() {
        new Thread(this::waitConnection).start();
    }

    private void waitConnection() {
        try (
                ServerSocket serverSocket = new ServerSocket(8080);
                var executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");
                executor.execute(new ClientConnection(clientSocket, requestProcessor));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
