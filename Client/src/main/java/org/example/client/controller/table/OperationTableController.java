package org.example.client.controller.table;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import lombok.Setter;
import org.example.dto.model.DecreaseOperationDTO;
import org.example.dto.model.IncreaseOperationDTO;
import org.example.dto.model.OperationDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class OperationTableController {
    @FXML
    private TableView<OperationDTO> operationTableView;

    @Setter
    private OperationActionListener listener;

    @FXML
    public void initialize() {
        setupTable();
    }

    public void bindItems(ObservableList<OperationDTO> items) {
        operationTableView.setItems(items);
    }

    private void setupTable() {
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
}
