package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
import org.example.client.viewModel.OperationHistoryViewModel;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

public class OperationHistoryController extends BaseController<OperationHistoryViewModel> {
    @FXML
    private ListView<OperationDTO> operationListView;

    @Override
    public void init() {
    }

    @Override
    protected void bindViewModel() {
        setupListView();
        operationListView.setItems(viewModel.getOperations());
    }

    @FXML
    public void handleOpenBudget() {
        SceneManager.getInstance().loadScene(Scene.BUDGET);
    }

    private void setupListView() {
        operationListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(OperationDTO operation, boolean empty) {
                super.updateItem(operation, empty);

                if (empty || operation == null) {
                    setText(null);
                    return;
                }

                String type = "";
                String category = "";
                if (operation instanceof IncreaseOperationDTO increaseOperationDTO) {
                    type = "PLUS";
                    category = increaseOperationDTO.getCategory().getName();
                } else if (operation instanceof DecreaseOperationDTO decreaseOperationDTO) {
                    type = "MINUS";
                    category = decreaseOperationDTO.getCategory().getName();
                }

                setText(String.format("%d. Тип: %s, Сумма: %d, Категория: %s", getIndex() + 1, type, operation.getAmount(), category));
            }
        });
    }
}
