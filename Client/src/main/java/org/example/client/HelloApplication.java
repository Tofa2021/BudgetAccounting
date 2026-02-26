package org.example.client;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.dto.Request;
import org.example.dto.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.Scanner;

public class HelloApplication extends Application {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws InterruptedException {
        while (true) {
            try (Socket clientSocket = new Socket("localhost", 8080);
                 ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())
            ) {
                while (true) {
                    String word = scanner.nextLine();
                    if (word.equals("stop")) {
                        out.writeObject(new Request("Stop", Map.of()));
                        out.flush();
                        System.exit(10);
                    }

                    out.writeObject(new Request("Print", Map.of("Text", word)));
                    out.flush();

                    Response response = (Response) in.readObject();
                    System.out.println(response.getStatus());
                }
            } catch (SocketException e) {
                if (e instanceof ConnectException || e.getMessage().equals("Connection reset by peer")) {
                    System.out.println("Cannot connect to server");
                    Thread.sleep(1000);
                } else {
                    throw new RuntimeException(e);
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}