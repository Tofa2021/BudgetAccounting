package org.example.connection;

import org.example.Controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class ConnectionManager {
    public void start(Controller controller) {
        try (
                ServerSocket serverSocket = new ServerSocket(8080);
                var executor = Executors.newVirtualThreadPerTaskExecutor()
        ) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.execute(new ConnectionHandler(clientSocket, controller));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
