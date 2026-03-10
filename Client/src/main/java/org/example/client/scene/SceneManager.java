package org.example.client.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.client.RRManager;
import org.example.client.controller.BaseController;
import org.example.client.viewModel.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {
    private static SceneManager instance;
    private final Map<Scene, SceneInfo> scenes = new HashMap<>();
    private Stage stage;

    private SceneManager() {
    }

    public static SceneManager getInstance() {
        if (instance == null) {
            instance = new SceneManager();
        }
        return instance;
    }

    public void init(Stage stage, RRManager rrManager, Scene firstScene) {
        this.stage = stage;
        stage.setMaximized(true);

        stage.setTitle("BudgetAccounting");

        OperationHistoryViewModel operationHistoryViewModel = new OperationHistoryViewModel(rrManager);

        scenes.put(Scene.AUTH, new SceneInfo("/org/example/client/auth-view.fxml", new AuthViewModel(rrManager)));
        scenes.put(Scene.REGISTRATION, new SceneInfo("/org/example/client/registration-view.fxml", new RegistrationViewModel(rrManager)));
        scenes.put(Scene.BUDGET, new SceneInfo("/org/example/client/budget-view.fxml", new BudgetViewModel(rrManager)));
        scenes.put(Scene.OPERATION_HISTORY, new SceneInfo("/org/example/client/operation-history-view.fxml", operationHistoryViewModel));
        scenes.put(Scene.GRAPHICS, new SceneInfo("/org/example/client/graphics-view.fxml", operationHistoryViewModel));

        initFirstScene(stage, firstScene);
        loadScene(firstScene);
    }

    private void initFirstScene(Stage stage, Scene firstScene) {
        try {
            SceneInfo sceneInfo = scenes.get(firstScene);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(sceneInfo.fxmlPath())
            );

            Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load scene " + firstScene.name(), e);
        }
    }

    public void loadScene(Scene loadScene) {
        try {
            SceneInfo sceneInfo = scenes.get(loadScene);

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(sceneInfo.fxmlPath())
            );

            Parent root = loader.load();

            BaseController<BaseViewModel> controller = loader.getController();
            controller.setViewModel(sceneInfo.viewModel());

            stage.getScene().setRoot(root);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load scene " + loadScene.name(), e);
        }
    }
}
