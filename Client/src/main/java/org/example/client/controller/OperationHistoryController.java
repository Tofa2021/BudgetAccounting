package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
import org.example.client.viewModel.OperationHistoryViewModel;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

public class OperationHistoryController extends BaseController<OperationHistoryViewModel> {
    @FXML
    private TableView<OperationDTO> operationTableView;

    @Override
    public void init() {
    }

    @Override
    protected void bindViewModel() {
        setupTableView();
        operationTableView.setItems(viewModel.getOperations());
    }

    @FXML
    public void handleOpenBudget() {
        SceneManager.getInstance().loadScene(Scene.BUDGET);
    }

    private void setupTableView() {
        setupRowFactory();

        TableColumn<OperationDTO, Integer> indexColumn = new TableColumn<>("№");
        indexColumn.setCellFactory(col -> new TableCell<OperationDTO, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        indexColumn.setPrefWidth(50);

        TableColumn<OperationDTO, String> typeColumn = new TableColumn<>("Тип");
        typeColumn.setCellValueFactory(cellData -> {
            OperationDTO operation = cellData.getValue();
            String type = "";
            if (operation instanceof IncreaseOperationDTO) {
                type = "+";
            } else if (operation instanceof DecreaseOperationDTO) {
                type = "-";
            }
            return new javafx.beans.property.SimpleStringProperty(type);
        });
        typeColumn.setPrefWidth(80);

        TableColumn<OperationDTO, Integer> amountColumn = new TableColumn<>("Сумма");
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setPrefWidth(100);

        TableColumn<OperationDTO, String> categoryColumn = new TableColumn<>("Категория");
        categoryColumn.setCellValueFactory(cellData -> {
            OperationDTO operation = cellData.getValue();
            String category = "";
            if (operation instanceof IncreaseOperationDTO increaseOperationDTO) {
                category = increaseOperationDTO.getCategory().getName();
            } else if (operation instanceof DecreaseOperationDTO decreaseOperationDTO) {
                category = decreaseOperationDTO.getCategory().getName();
            }
            return new javafx.beans.property.SimpleStringProperty(category);
        });
        categoryColumn.setPrefWidth(150);

        // TODO dateColumn

        TableColumn<OperationDTO, Void> actionsColumn = new TableColumn<>("Действия");
        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OperationDTO, Void> call(final TableColumn<OperationDTO, Void> param) {
                return new TableCell<>() {
                    private final Button updateButton = new Button("✏️");
                    private final Button deleteButton = new Button("🗑️");
                    private final HBox pane = new HBox(5, updateButton, deleteButton);

                    {
                        updateButton.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-size: 12px;");
                        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 12px;");

                        updateButton.setPrefWidth(60);
                        deleteButton.setPrefWidth(60);

                        pane.setAlignment(Pos.CENTER);

                        updateButton.setOnAction(event -> {
                            OperationDTO operation = getTableView().getItems().get(getIndex());
                            handleUpdateOperation(operation);
                        });

                        deleteButton.setOnAction(event -> {
                            OperationDTO operation = getTableView().getItems().get(getIndex());
                            handleDeleteOperation(operation);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(pane);
                        }
                    }
                };
            }
        });
        actionsColumn.setPrefWidth(200);

        operationTableView.getColumns().addAll(
                indexColumn, typeColumn, amountColumn, categoryColumn, actionsColumn
        );

        operationTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void setupRowFactory() {
        operationTableView.setRowFactory(tv -> new TableRow<OperationDTO>() {
            @Override
            protected void updateItem(OperationDTO operation, boolean empty) {
                super.updateItem(operation, empty);

                getStyleClass().removeAll("row-plus", "row-minus");

                if (operation == null || empty) {
                    setStyle("");
                    return;
                }

                if (operation instanceof IncreaseOperationDTO) {
                    setStyle("-fx-background-color: #e8f5e8;");
                } else if (operation instanceof DecreaseOperationDTO) {
                    setStyle("-fx-background-color: #ffebee;");
                }
            }
        });
    }

    private void handleDeleteOperation(OperationDTO operationDTO) {
        operationTableView.getItems().remove(operationDTO);
        viewModel.delete(operationDTO);
    }

    private void handleUpdateOperation(OperationDTO operationDTO) {
        Stage currentStage = (Stage) operationTableView.getScene().getWindow();
        UpdateOperationOverlay overlay = new UpdateOperationOverlay(
                currentStage,
                operationDTO,
                () -> viewModel.update(operationDTO)
        );

        overlay.show();
    }
}
