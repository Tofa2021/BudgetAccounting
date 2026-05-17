package org.example.client.view;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.client.Utils;
import org.example.client.viewModel.OperationViewModel;
import org.example.dto.OperationDTO;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class OperationView extends BaseView<OperationViewModel> {
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private VBox incomes;
    @FXML
    private VBox expenses;
    @FXML
    private VBox profits;
    @FXML
    private VBox amountOperations;
    @FXML
    private ListView<OperationDTO> operationsList;
    @FXML
    private Button addButton;

    @Override
    protected void onViewModelSet() {
        setupListView();
        bind();
        setupContextMenu();
    }

    private void setupListView() {
        operationsList.setCellFactory(listView -> new OperationCell());
        operationsList.setPlaceholder(new Label("Нет операций\nНажмите + чтобы добавить"));
    }

    private void bind() {
        operationsList.setItems(viewModel.getOperations());

        viewModel.getIncomes().addListener((ListChangeListener<? super String>) c -> {
            updateIncomes();
        });

        viewModel.getExpenses().addListener((ListChangeListener<? super String>) c -> {
            updateExpenses();
        });

        viewModel.getProfits().addListener((ListChangeListener<? super String>) c -> {
            updateProfits();
        });

        viewModel.getAmountOperations().addListener((ListChangeListener<? super String>) c -> {
            updateAmountOperations();
        });
    }

    private void updateIncomes() {
        incomes.getChildren().clear();
        for (String text : viewModel.getIncomes()) {
            addIncomeLabel(text);
        }
    }

    private void updateExpenses() {
        expenses.getChildren().clear();
        for (String text : viewModel.getExpenses()) {
            addExpenseLabel(text);
        }
    }

    private void updateProfits() {
        profits.getChildren().clear();
        for (String text : viewModel.getProfits()) {
            addProfitLabel(text);
        }
    }

    private void updateAmountOperations() {
        amountOperations.getChildren().clear();
        for (String text : viewModel.getAmountOperations()) {
            addAmountOperationsLabel(text);
        }
    }

    private void addIncomeLabel(String text) {
        Label label = createLabel(text, "green-bold-16-text");
        incomes.getChildren().add(label);
    }

    private void addExpenseLabel(String text) {
        Label label = createLabel(text, "red-bold-16-text");
        expenses.getChildren().add(label);
    }

    private void addProfitLabel(String text) {
        Label label = createLabel(text, "blue-bold-16-text");
        profits.getChildren().add(label);
    }

    private void addAmountOperationsLabel(String text) {
        Label label = createLabel(text, "grey-bold-16-text");
        amountOperations.getChildren().add(label);
    }

    private Label createLabel(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.prefWidth(222);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem editItem = new MenuItem("✏ Редактировать");
        editItem.setOnAction(e -> {
            OperationDTO selected = operationsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showEditDialog(selected);
            }
        });

        MenuItem deleteItem = new MenuItem("🗑 Удалить");
        deleteItem.setStyle("-fx-text-fill: #d32f2f;");
        deleteItem.setOnAction(e -> {
            OperationDTO selected = operationsList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                showDeleteConfirmation(selected);
            }
        });

        contextMenu.getItems().addAll(editItem, new SeparatorMenuItem(), deleteItem);
        operationsList.setContextMenu(contextMenu);
    }

    @FXML
    private void handleAddButton() {
        viewModel.handleCreatOperationButton();
    }

    private void showEditDialog(OperationDTO operation) {
        Dialog<OperationDTO> dialog = new Dialog<>();
        dialog.setTitle("Редактирование операции");
        dialog.setHeaderText("Измените данные операции");

        ButtonType saveButton = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(20));

        TextField descriptionField = new TextField(operation.getDescription());
        descriptionField.setPromptText("Описание");

        TextField amountField = new TextField(operation.getAmount() != null ? operation.getAmount().toString() : "");
        amountField.setPromptText("Сумма");

        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("INCOME", "EXPENSE");
        typeCombo.setValue(operation.getType());

        TextField categoryField = new TextField(operation.getCategoryName() != null ? operation.getCategoryName() : "");
        categoryField.setPromptText("Категория");

        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        if (operation.getDateTime() != null) {
            var localDateTime = operation.getDateTime().atZone(ZoneId.systemDefault());
            currentDate = localDateTime.toLocalDate();
            currentTime = localDateTime.toLocalTime();
        }

        DatePicker datePicker = new DatePicker(currentDate);
        TextField timeField = new TextField(currentTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        timeField.setPromptText("HH:mm");

        int row = 0;
        form.add(new Label("Описание:"), 0, row);
        form.add(descriptionField, 1, row++);
        form.add(new Label("Сумма:"), 0, row);
        form.add(amountField, 1, row++);
        form.add(new Label("Тип:"), 0, row);
        form.add(typeCombo, 1, row++);
        form.add(new Label("Категория:"), 0, row);
        form.add(categoryField, 1, row++);
        form.add(new Label("Дата:"), 0, row);
        form.add(datePicker, 1, row++);
        form.add(new Label("Время:"), 0, row);
        form.add(timeField, 1, row++);

        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    String description = descriptionField.getText().trim();
                    if (description.isEmpty()) {
                        Platform.runLater(() -> showError("Введите описание"));
                        return null;
                    }

                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        Platform.runLater(() -> showError("Сумма должна быть больше 0"));
                        return null;
                    }

                    String type = typeCombo.getValue();
                    String category = categoryField.getText().trim();

                    LocalDate date = datePicker.getValue();
                    LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                    Instant instant = LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant();

                    return new OperationDTO(
                            operation.getId(),
                            operation.getAccountId(),
                            description,
                            amount,
                            instant,
                            operation.getAccountMemberId(),
                            operation.getCategoryId(),
                            category.isEmpty() ? null : category,
                            type,
                            operation.getCurrency()
                    );
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Ошибка ввода данных: " + e.getMessage()));
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedDto -> {
            viewModel.update(updatedDto);
            showSuccess("Операция обновлена");
        });
    }

    private void showDeleteConfirmation(OperationDTO operation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление операции");
        alert.setHeaderText("Удалить операцию?");

        String dateStr = operation.getDateTime() != null ?
                dateFormatter.format(operation.getDateTime().atZone(ZoneId.systemDefault())) : "без даты";

        alert.setContentText(String.format(
                "%s\n%s\nСумма: %.2f BYN",
                operation.getDescription(),
                dateStr,
                operation.getAmount()
        ));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                viewModel.delete(operation);
                showSuccess("Операция удалена");
            }
        });
    }

    private static class LocalDateTimeHolder {
        LocalDate date;
        LocalTime time;

        public LocalDateTimeHolder() {
        }

        public LocalDateTimeHolder(LocalDate date, LocalTime time) {
            this.date = date;
            this.time = time;
        }

        public static LocalDateTimeHolder fromInstant(Instant instant) {
            var localDateTime = instant.atZone(ZoneId.systemDefault());
            LocalDateTimeHolder holder = new LocalDateTimeHolder();
            holder.date = localDateTime.toLocalDate();
            holder.time = localDateTime.toLocalTime();
            return holder;
        }

        public Instant toInstant() {
            return LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant();
        }
    }

    private class OperationCell extends ListCell<OperationDTO> {
        private final HBox card = new HBox();
        private final VBox leftContent = new VBox();
        private final VBox centerContent = new VBox();
        private final Label typeLabel = new Label();
        private final Label categoryLabel = new Label();
        private final Label descriptionLabel = new Label();
        private final Label dateLabel = new Label();
        private final Label timeLabel = new Label();
        private final Label amountLabel = new Label();

        public OperationCell() {
            card.setPadding(new Insets(12, 15, 12, 15));
            card.setSpacing(15);
            card.setAlignment(Pos.CENTER_LEFT);
            card.getStyleClass().add("operation-card");

            typeLabel.getStyleClass().add("operation-type");
            typeLabel.setMinWidth(80);
            typeLabel.setAlignment(Pos.CENTER);

            categoryLabel.getStyleClass().add("operation-category");

            leftContent.getChildren().addAll(typeLabel, categoryLabel);
            leftContent.setSpacing(5);
            leftContent.setAlignment(Pos.CENTER_LEFT);
            leftContent.setMinWidth(120);

            descriptionLabel.getStyleClass().add("operation-description");
            descriptionLabel.setWrapText(true);

            HBox dateTimeBox = new HBox(10);
            dateLabel.getStyleClass().add("operation-date");
            timeLabel.getStyleClass().add("operation-time");
            dateTimeBox.getChildren().addAll(dateLabel, timeLabel);

            centerContent.getChildren().addAll(descriptionLabel, dateTimeBox);
            centerContent.setSpacing(5);
            centerContent.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(centerContent, Priority.ALWAYS);

            amountLabel.getStyleClass().add("operation-amount");
            amountLabel.setAlignment(Pos.CENTER_RIGHT);
            amountLabel.setMinWidth(120);

            card.getChildren().addAll(leftContent, centerContent, amountLabel);
        }

        @Override
        protected void updateItem(OperationDTO operation, boolean empty) {
            super.updateItem(operation, empty);

            if (empty || operation == null) {
                setGraphic(null);
                return;
            }

            String type = operation.getType();
            typeLabel.setText(type != null ? type : "");

            String category = operation.getCategoryName();
            categoryLabel.setText(category != null ? category : "Без категории");

            String description = operation.getDescription();
            descriptionLabel.setText(description != null && !description.isEmpty() ? description : "—");

            if (operation.getDateTime() != null) {
                var dateTime = operation.getDateTime().atZone(ZoneId.systemDefault());
                dateLabel.setText(dateFormatter.format(dateTime));
                timeLabel.setText(timeFormatter.format(dateTime));
            } else {
                dateLabel.setText("—");
                timeLabel.setText("—");
            }

            String formattedAmount = Utils.convertBigDecimalToString(operation.getAmount(), operation.getType(), operation.getCurrency());

            if ("INCOME".equals(type)) {
                amountLabel.getStyleClass().removeAll("expense", "income");
                amountLabel.getStyleClass().add("income");
            } else if ("EXPENSE".equals(type)) {
                amountLabel.getStyleClass().removeAll("expense", "income");
                amountLabel.getStyleClass().add("expense");
            }
            amountLabel.setText(formattedAmount);

            setGraphic(card);
        }
    }
}