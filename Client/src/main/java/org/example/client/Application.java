package org.example.client;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.client.connection.ServerInteractionManager;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

public class Application extends javafx.application.Application {
    private ServerInteractionManager serverInteractionManager;

    public Application() {
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        serverInteractionManager = new ServerInteractionManager();
        SceneManager.getInstance().init(stage, serverInteractionManager, Scene.AUTH);

        stage.setOnCloseRequest(event -> {
            close();
        });
    }

    private void close() {
        serverInteractionManager.close();
        Platform.exit();
    }

    @Override
    public void stop() {
        close();
    }
}