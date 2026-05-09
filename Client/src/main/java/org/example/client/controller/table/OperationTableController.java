package org.example.client.controller.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Callback;
import lombok.Setter;
import org.example.dto.OperationDTO;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class OperationTableController {
    @FXML
    private TableView<OperationDTO> operationTableView;

    @FXML
    private TableColumn<OperationDTO, Integer> indexColumn;
    @FXML
    private TableColumn<OperationDTO, String> typeColumn;
    @FXML
    private TableColumn<OperationDTO, Double> amountColumn;
    @FXML
    private TableColumn<OperationDTO, String> categoryColumn;
    @FXML
    private TableColumn<OperationDTO, String> dateTimeColumn;
    @FXML
    private TableColumn<OperationDTO, Void> actionsColumn;

    @Setter
    private OperationActionListener listener;

    @FXML
    public void initialize() {
        setupColumns();
        setupRowFactory();
        setupTableStyle();
    }

    public void bindItems(ObservableList<OperationDTO> items) {
        operationTableView.setItems(items);
    }

    private void setupTableStyle() {
        operationTableView.setStyle(
                "-fx-font-family: 'Segoe UI', 'System';" +
                        "-fx-font-size: 13px;" +
                        "-fx-background-color: white;"
        );

        operationTableView.widthProperty().addListener((obs, oldVal, newVal) -> {
            operationTableView.lookupAll("TableColumnHeader").forEach(header -> {
                header.setStyle(
                        "-fx-background-color: #f8f9fa;" +
                                "-fx-border-color: #dee2e6;" +
                                "-fx-border-width: 0 0 2 0;" +
                                "-fx-padding: 8px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 13px;" +
                                "-fx-text-fill: #495057;"
                );
            });
        });

        Label placeholder = new Label("Нет операций");
        placeholder.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #6c757d;" +
                        "-fx-padding: 20px;"
        );
        operationTableView.setPlaceholder(placeholder);
    }

    private void setupColumns() {
        indexColumn.setCellFactory(col -> new TableCell<>() {
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
        indexColumn.setStyle("-fx-alignment: CENTER;");

        typeColumn.setCellValueFactory(cellData -> {
            OperationDTO operation = cellData.getValue();
            String type = "";
            if (operation instanceof IncreaseOperationDTO) {
                type = "Доход";
            } else if (operation instanceof DecreaseOperationDTO) {
                type = "Расход";
            }
            return new SimpleStringProperty(type);
        });
        typeColumn.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(8);
            private final Circle indicator = new Circle(5);
            private final Label typeLabel = new Label();

            {
                container.setAlignment(Pos.CENTER_LEFT);
                indicator.setStroke(Color.TRANSPARENT);
                typeLabel.setStyle("-fx-font-weight: 600;");
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    typeLabel.setText(item);

                    if (item.equals("Доход")) {
                        indicator.setFill(Color.web("#28a745"));
                        typeLabel.setTextFill(Color.web("#28a745"));
                    } else {
                        indicator.setFill(Color.web("#dc3545"));
                        typeLabel.setTextFill(Color.web("#dc3545"));
                    }

                    container.getChildren().setAll(indicator, typeLabel);
                    setGraphic(container);
                }
            }
        });
        typeColumn.setStyle("-fx-alignment: CENTER_LEFT;");

        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", amount));
                }
            }
        });
        amountColumn.setStyle("-fx-alignment: CENTER_RIGHT; -fx-padding: 0 15 0 0;");

        categoryColumn.setCellValueFactory(cellData -> {
            OperationDTO operation = cellData.getValue();
            String category = "";
            if (operation instanceof IncreaseOperationDTO increaseOperationDTO) {
                category = increaseOperationDTO.getCategory().getName();
            } else if (operation instanceof DecreaseOperationDTO decreaseOperationDTO) {
                category = decreaseOperationDTO.getCategory().getName();
            }
            return new SimpleStringProperty(category);
        });
        categoryColumn.setStyle("-fx-alignment: CENTER_LEFT;");

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
        dateTimeColumn.setStyle("-fx-alignment: CENTER;");

        actionsColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<OperationDTO, Void> call(final TableColumn<OperationDTO, Void> param) {
                return new TableCell<>() {
                    private final Button updateButton = new Button("✏");
                    private final Button deleteButton = new Button("🗑");
                    private final HBox pane = new HBox(5, updateButton, deleteButton);

                    {
                        updateButton.setStyle(
                                "-fx-background-color: #ffc107; " +
                                        "-fx-background-radius: 5; " +
                                        "-fx-text-fill: #212529; " +
                                        "-fx-font-size: 12px; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-padding: 5 10 5 10; " +
                                        "-fx-cursor: hand;"
                        );
                        updateButton.setPrefWidth(60);

                        deleteButton.setStyle(
                                "-fx-background-color: #dc3545; " +
                                        "-fx-background-radius: 5; " +
                                        "-fx-text-fill: white; " +
                                        "-fx-font-size: 12px; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-padding: 5 10 5 10; " +
                                        "-fx-cursor: hand;"
                        );
                        deleteButton.setPrefWidth(60);

                        pane.setAlignment(Pos.CENTER);

                        updateButton.setOnAction(event -> {
                            OperationDTO operation = getTableView().getItems().get(getIndex());
                            listener.onOperationUpdated(operation);
                        });

                        deleteButton.setOnAction(event -> {
                            OperationDTO operation = getTableView().getItems().get(getIndex());
                            listener.onOperationDeleted(operation);
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
        actionsColumn.setStyle("-fx-alignment: CENTER;");
    }

    private void setupRowFactory() {
        operationTableView.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(OperationDTO operation, boolean empty) {
                super.updateItem(operation, empty);
                if (operation == null || empty) {
                    setStyle("");
                }
            }
        });
    }
}