package org.example.connection;

import org.example.Controller;
import org.example.dto.request.Request;
import org.example.dto.response.Response;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket clientSocket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final Controller controller;

    public ConnectionHandler(Socket socket, Controller controller) throws IOException {
        clientSocket = socket;
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Request request = (Request) in.readObject();
                send(controller.process(request));
            }
        } catch (EOFException e) {
            System.out.println("Client disconnected normally");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error in connection: " + e.getMessage());
        } finally {
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
}
