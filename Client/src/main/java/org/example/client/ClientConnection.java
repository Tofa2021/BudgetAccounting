package org.example.client;

import org.example.dto.Request;
import org.example.dto.Response;
import org.example.dto.Status;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class ClientConnection {
    private final BlockingQueue<Request> requestQueue = new SynchronousQueue<>();
    private final BlockingQueue<Response> responseQueue;

    public ClientConnection(BlockingQueue<Response> responseQueue) {
        this.responseQueue = responseQueue;
        new Thread(this::start).start();
    }

    private void start() {
        while (true) {
            try (
                    Socket socket = new Socket("localhost", 8080);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                while (true) {
                    Response response = send(in, out, requestQueue.take());
                    responseQueue.put(response);
                }
            } catch (ConnectException e) {
                System.out.println("Cannot connect to Server");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
            } catch (SocketException e) {
                System.err.println("Loss connection");
                try {
                    responseQueue.put(new Response(Status.CONNECTION_ERROR, Map.of()));
                    Thread.sleep(1000);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }

            } catch (IOException | InterruptedException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Response send(ObjectInputStream in, ObjectOutputStream out, Request request) throws IOException, ClassNotFoundException {
        writeRequest(out, request);
        return readResponse(in);
    }

    private void writeRequest(ObjectOutputStream out, Request request) throws IOException {
        out.writeObject(request);
    }

    private Response readResponse(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (Response) in.readObject();
    }

    public void putRequest(Request request) {
        try {
            requestQueue.put(request);
        } catch (InterruptedException e) {
            throw new RuntimeException("InterruptedException", e);
        }
    }
}
