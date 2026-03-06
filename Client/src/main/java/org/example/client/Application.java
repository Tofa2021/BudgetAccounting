package org.example.client;

import javafx.stage.Stage;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

public class Application extends javafx.application.Application {
    public Application() {
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) {
        SceneManager.getInstance().init(stage, new RRManager(), Scene.AUTH);
    }
}