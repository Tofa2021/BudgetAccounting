package org.example.client.connection;

import org.example.request.RequestEnvelope;
import org.example.response.Response;
import org.example.response.Status;

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
    private final BlockingQueue<RequestEnvelope> requestQueue;
    private final BlockingQueue<Response> responseQueue;
    private final Thread connectionThread;

    public ClientConnection(BlockingQueue<Response> responseQueue) {
        this.requestQueue = new SynchronousQueue<>();
        this.responseQueue = responseQueue;
        connectionThread = new Thread(this::start);
        connectionThread.start();
    }

    private void start() {
        while (true) {
            try (
                    Socket socket = new Socket("localhost", 8080);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                System.out.println("Connected to Server");
                while (true) {
                    RequestEnvelope requestEnvelope = requestQueue.take();
                    Response response = send(in, out, requestEnvelope);
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

    private Response send(ObjectInputStream in, ObjectOutputStream out, RequestEnvelope requestEnvelope) throws IOException, ClassNotFoundException {
        writeRequest(out, requestEnvelope);
        return readResponse(in);
    }

    private void writeRequest(ObjectOutputStream out, RequestEnvelope requestEnvelope) throws IOException {
        out.writeObject(requestEnvelope);
    }

    private Response readResponse(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (Response) in.readObject();
    }

    public void putRequest(RequestEnvelope requestEnvelope) {
        try {
            requestQueue.put(requestEnvelope);
        } catch (InterruptedException e) {
            throw new RuntimeException("InterruptedException", e);
        }
    }

    public void close() {
        connectionThread.interrupt();
    }
}
