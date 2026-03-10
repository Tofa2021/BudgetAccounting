package org.example.client.controller;

import javafx.fxml.FXML;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

public class NavigationController {

    @FXML
    public void handleOpenOperationHistory() {
        SceneManager.getInstance().loadScene(Scene.OPERATION_HISTORY);
    }

    @FXML
    public void handleOpenGraphicsButton() {
        SceneManager.getInstance().loadScene(Scene.GRAPHICS);
    }

    @FXML
    public void handleOpenBudget() {
        SceneManager.getInstance().loadScene(Scene.BUDGET);
    }

    @FXML
    public void handleLogout() {
        SceneManager.getInstance().loadScene(Scene.AUTH);
    }
}
