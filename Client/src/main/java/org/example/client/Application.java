package org.example.client;

import javafx.application.Platform;
import javafx.stage.Stage;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

public class Application extends javafx.application.Application {
    private RRManager rrManager;

    public Application() {
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        rrManager = new RRManager();
        SceneManager.getInstance().init(stage, rrManager, Scene.AUTH);

        stage.setOnCloseRequest(event -> {
            close();
        });
    }

    private void close() {
        rrManager.close();
        Platform.exit();
    }

    @Override
    public void stop() {
        close();
    }
}