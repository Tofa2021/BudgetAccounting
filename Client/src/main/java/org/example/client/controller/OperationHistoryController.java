package org.example.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
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

import java.time.*;
import java.time.format.DateTimeFormatter;

public class OperationHistoryController extends BaseController<OperationHistoryViewModel> {
    @FXML
    private TableView<OperationDTO> operationTableView;
    @FXML
    private ToggleButton weekButton;
    @FXML
    private ToggleButton monthButton;
    @FXML
    private ToggleButton threeMonthsButton;
    @FXML
    private ToggleButton halfYearButton;
    @FXML
    private ToggleButton yearButton;
    @FXML
    private ToggleButton allButton;
    @FXML
    private Label periodInfoLabel;

    private ToggleGroup periodToggleGroup;

    @Override
    public void init() {
        setupPeriodSelector();
    }

    @Override
    protected void bindViewModel() {
        setupTableView();
        operationTableView.setItems(viewModel.getOperations());
    }

    private void setupPeriodSelector() {
        periodToggleGroup = new ToggleGroup();

        weekButton.setToggleGroup(periodToggleGroup);
        monthButton.setToggleGroup(periodToggleGroup);
        threeMonthsButton.setToggleGroup(periodToggleGroup);
        halfYearButton.setToggleGroup(periodToggleGroup);
        yearButton.setToggleGroup(periodToggleGroup);
        allButton.setToggleGroup(periodToggleGroup);

        periodToggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                resetButtonStyles();

                ToggleButton selectedButton = (ToggleButton) newToggle;
                selectedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");

                loadOperationsForPeriod(selectedButton);
            }
        });

        allButton.setSelected(true);

        updatePeriodInfo("Все время", null);
    }

    @FXML
    private void handlePeriodClick(ActionEvent event) {
        ToggleButton clickedButton = (ToggleButton) event.getSource();
        resetButtonStyles();
        clickedButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        loadOperationsForPeriod(clickedButton);
    }

    private void resetButtonStyles() {
        String defaultStyle = "-fx-background-color: #e0e0e0; -fx-text-fill: black;";
        weekButton.setStyle(defaultStyle);
        monthButton.setStyle(defaultStyle);
        threeMonthsButton.setStyle(defaultStyle);
        halfYearButton.setStyle(defaultStyle);
        yearButton.setStyle(defaultStyle);
        allButton.setStyle(defaultStyle);
    }

    private void loadOperationsForPeriod(ToggleButton selectedButton) {
        if (selectedButton == weekButton) {
            viewModel.loadRecentOperations(7);
            updatePeriodInfo("последние 7 дней", 7);
        } else if (selectedButton == monthButton) {
            viewModel.loadRecentOperations(30);
            updatePeriodInfo("последние 30 дней", 30);
        } else if (selectedButton == threeMonthsButton) {
            viewModel.loadRecentOperations(90);
            updatePeriodInfo("последние 90 дней", 90);
        } else if (selectedButton == halfYearButton) {
            viewModel.loadRecentOperations(180);
            updatePeriodInfo("полгода", 180);
        } else if (selectedButton == yearButton) {
            viewModel.loadRecentOperations(365);
            updatePeriodInfo("год", 365);
        } else if (selectedButton == allButton) {
            viewModel.refreshOperations();
            updatePeriodInfo("все время", null);
        }
    }

    private void updatePeriodInfo(String periodName, Integer days) {
        if (days != null) {
            LocalDate fromDate = LocalDate.now().minusDays(days);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            periodInfoLabel.setText(String.format("Показаны операции с %s по %s (за %s)",
                    fromDate.format(formatter),
                    LocalDate.now().format(formatter),
                    periodName));
        } else {
            periodInfoLabel.setText("Показаны все операции");
        }
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

        TableColumn<OperationDTO, String> dateTimeColumn = new TableColumn<>("Дата");
        dateTimeColumn.setCellValueFactory(cellData -> {
            OperationDTO operation = cellData.getValue();
            Instant dateTime = operation.getDateTime();
            ZonedDateTime userDateTime = dateTime.atZone(ZoneId.systemDefault());
            return new SimpleStringProperty(userDateTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        });
        dateTimeColumn.setComparator((dateStr1, dateStr2) -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            LocalDateTime date1 = LocalDateTime.parse(dateStr1, formatter);
            LocalDateTime date2 = LocalDateTime.parse(dateStr2, formatter);
            return date1.compareTo(date2);
        });

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
                indexColumn, typeColumn, amountColumn, categoryColumn, dateTimeColumn, actionsColumn
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
