package org.example.client.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
import org.example.client.viewModel.OperationHistoryViewModel;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class OperationHistoryController extends BaseController<OperationHistoryViewModel> implements PeriodSelectionListener {
    @FXML
    public ComboBox<String> categoryFilterComboBox;
    @FXML
    public ComboBox<String> typeFilterComboBox;
    @FXML
    public DatePicker dateFromPicker;
    @FXML
    public DatePicker dateToPicker;
    @FXML
    public Button clearDateButton;
    @FXML
    public TextField minAmountField;
    @FXML
    public TextField maxAmountField;
    @FXML
    public Button applyFiltersButton;
    @FXML
    public Button clearFiltersButton;
    @FXML
    public Label filterResultsLabel;
    @FXML
    private VBox periodSelect;

    @FXML
    private TableView<OperationDTO> operationTableView;
    @FXML
    private PeriodSelectController periodSelectController;

    @FXML
    public void initialize() {
        setupFilters();
        setupTableView();
        setupPeriodSelector();
    }

    @Override
    protected void bindViewModel() {
        operationTableView.setItems(viewModel.getOperations());
        viewModel.loadRecentOperations(7);
    }

    private void setupPeriodSelector() {
        periodSelectController.setListener(this);
    }

    private void setupFilters() {
        typeFilterComboBox.getItems().setAll("Все", "+", "-");
        typeFilterComboBox.setValue("Все");

        categoryFilterComboBox.getItems().setAll("Все категории");
        categoryFilterComboBox.setValue("Все категории");

        typeFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateCategoryFilter(newVal);
        });
    }

    private void updateCategoryFilter(String type) {
        categoryFilterComboBox.getItems().clear();
        categoryFilterComboBox.getItems().add("Все категории");

        switch (type) {
            case "+" -> {
                categoryFilterComboBox.getItems().addAll(Arrays.stream(
                                IncreaseOperationCategory.values())
                        .map(IncreaseOperationCategory::getName)
                        .toList());
            }
            case "-" -> categoryFilterComboBox.getItems().addAll(Arrays.stream(
                            DecreaseOperationCategory.values())
                    .map(DecreaseOperationCategory::getName)
                    .toList());
        }
        categoryFilterComboBox.setValue("Все категории");
    }

    @Override
    public void onPeriodSelected(int days) {
        viewModel.loadRecentOperations(days);
    }

    @Override
    public void onAllTimeSelected() {
        viewModel.refreshOperations();
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
        indexColumn.setPrefWidth(20);
        indexColumn.setSortable(false);

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
        actionsColumn.setSortable(false);

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

    @FXML
    private void handleApplyFilters() {
        String type = typeFilterComboBox.getValue();
        String category = categoryFilterComboBox.getValue();
        Instant dateFrom = dateFromPicker.getValue() != null
                ? dateFromPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                : null;
        Instant dateTo = dateToPicker.getValue() != null
                ? dateToPicker.getValue().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
                : null;
        Integer minAmount = minAmountField.getText().isEmpty()
                ? null
                : Integer.parseInt(minAmountField.getText());

        Integer maxAmount = maxAmountField.getText().isEmpty()
                ? null
                : Integer.parseInt(maxAmountField.getText());

        viewModel.getFilteredOperations(type, category, dateFrom, dateTo, minAmount, maxAmount);

        periodSelectController.resetButtonStyles();
    }

    @FXML
    private void handleClearFilters() {
        categoryFilterComboBox.getSelectionModel().selectFirst();
        typeFilterComboBox.getSelectionModel().selectFirst();
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        minAmountField.clear();
        maxAmountField.clear();
        operationTableView.setItems(viewModel.getOperations());

        periodSelectController.restorePeriod();
    }

    @FXML
    public void handleClearDates() {
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
    }
}
