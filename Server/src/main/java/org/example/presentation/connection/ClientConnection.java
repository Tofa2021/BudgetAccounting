package org.example.presentation.connection;

import org.example.dto.request.Request;
import org.example.dto.response.Response;
import org.example.presentation.RequestProcessor;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection implements Runnable {
    private final Socket clientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final RequestProcessor requestProcessor;

    public ClientConnection(Socket socket, RequestProcessor requestProcessor) throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.clientSocket = socket;
        this.requestProcessor = requestProcessor;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Request request = (Request) in.readObject();
                System.out.println("Received request: " + request.getAction() + " from " + clientSocket.getPort());
                Response response = requestProcessor.process(request);
                System.out.println("Send response: " + response.status());
                send(response);
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected normally");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in connection: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void send(Response response) {
        try {
            out.writeObject(response);
            out.flush();
            out.reset();
        } catch (IOException e) {
            System.err.println("Error sending data: " + e.getMessage());
            disconnect();
        }
    }

    private void disconnect() {
        try {
            in.close();
            out.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Socket closed");
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
