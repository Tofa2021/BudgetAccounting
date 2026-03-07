package org.example.client.controller;

import javafx.fxml.FXML;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
import org.example.client.viewModel.OperationHistoryViewModel;

public class OperationHistoryController extends BaseController<OperationHistoryViewModel> {
    @Override
    public void init() {

    }

    @Override
    protected void bindViewModel() {

    }

    @FXML
    public void handleOpenBudget() {
        SceneManager.getInstance().loadScene(Scene.BUDGET);
    }
}
