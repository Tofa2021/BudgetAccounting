package org.example.connection;

import org.example.RequestProcessor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class ConnectionManager {
    public void start(RequestProcessor requestProcessor) {
        try (
                ServerSocket serverSocket = new ServerSocket(8080);
                var executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ClientConnection(clientSocket, requestProcessor));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
