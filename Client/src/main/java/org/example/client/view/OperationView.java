package org.example.client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.example.client.viewModel.OperationViewModel;
import org.example.dto.OperationDTO;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class OperationView extends BaseView<OperationViewModel> {

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @FXML
    private ListView<OperationDTO> operationsList;
    @FXML
    private Button addButton;

    @Override
    protected void onViewModelSet() {
        setupListView();
        bindOperations();
        setupContextMenu();
    }

    private void setupListView() {
        operationsList.setCellFactory(listView -> new OperationCell());
        operationsList.setPlaceholder(new Label("📭 Нет операций\nНажмите + чтобы добавить"));
    }

    private void bindOperations() {
        operationsList.setItems(viewModel.getOperations());
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

        // Конвертер результата: собирает данные в НОВЫЙ объект, не ломая старый
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                try {
                    String description = descriptionField.getText().trim();
                    if (description.isEmpty()) {
                        Platform.runLater(() -> showTemporaryMessage("Введите описание", "error"));
                        return null;
                    }

                    BigDecimal amount = new BigDecimal(amountField.getText().trim());
                    if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                        Platform.runLater(() -> showTemporaryMessage("Сумма должна быть больше 0", "error"));
                        return null;
                    }

                    String type = typeCombo.getValue();
                    String category = categoryField.getText().trim();

                    LocalDate date = datePicker.getValue();
                    LocalTime time = LocalTime.parse(timeField.getText().trim(), DateTimeFormatter.ofPattern("HH:mm"));
                    Instant instant = LocalDateTime.of(date, time).atZone(ZoneId.systemDefault()).toInstant();

                    // Создаем копию DTO, сохраняя старые ID (чтобы не мутировать исходный объект раньше времени)
                    return new OperationDTO(
                            operation.getId(),
                            operation.getAccountId(),
                            description,
                            amount,
                            instant,
                            operation.getAccountMemberId(),
                            operation.getCategoryId(), // В реальной системе тут может потребоваться ID новой категории
                            category.isEmpty() ? null : category,
                            type
                    );
                } catch (Exception e) {
                    Platform.runLater(() -> showTemporaryMessage("Ошибка ввода данных: " + e.getMessage(), "error"));
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedDto -> {
            viewModel.update(updatedDto);
            showTemporaryMessage("✓ Операция обновлена", "success");
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
                showTemporaryMessage("✓ Операция удалена", "success");
            }
        });
    }

    private void showTemporaryMessage(String message, String type) {
        Tooltip tooltip = new Tooltip(message);

        String color;
        switch (type) {
            case "success":
                color = "#4caf50";
                break;
            case "error":
                color = "#f44336";
                break;
            default:
                color = "#2196f3";
        }

        tooltip.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 12px;");

        if (operationsList.getScene() != null) {
            tooltip.show(operationsList,
                    operationsList.getScene().getWindow().getX() + 200,
                    operationsList.getScene().getWindow().getY() + 100);

            // Безопасное скрытие через фоновый поток и Platform.runLater
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                // Перенаправляем задачу закрытия обратно в UI-поток
                Platform.runLater(tooltip::hide);
            }).start();
        }
    }

    // Вспомогательный класс для работы с датой/временем
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

    // Ячейка для отображения операции
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

            // Тип
            String type = operation.getType();
            typeLabel.setText(type != null ? type : "");

            // Категория
            String category = operation.getCategoryName();
            categoryLabel.setText(category != null ? category : "Без категории");

            // Описание
            String description = operation.getDescription();
            descriptionLabel.setText(description != null && !description.isEmpty() ? description : "—");

            // Дата и время
            if (operation.getDateTime() != null) {
                var dateTime = operation.getDateTime().atZone(ZoneId.systemDefault());
                dateLabel.setText(dateFormatter.format(dateTime));
                timeLabel.setText(timeFormatter.format(dateTime));
            } else {
                dateLabel.setText("—");
                timeLabel.setText("—");
            }

            // Сумма
            BigDecimal amount = operation.getAmount();
            String formattedAmount = String.format("%.2f BYN", amount.abs());

            if ("INCOME".equals(type)) {
                amountLabel.setText("+" + formattedAmount);
                amountLabel.getStyleClass().removeAll("expense", "income");
                amountLabel.getStyleClass().add("income");
            } else if ("EXPENSE".equals(type)) {
                amountLabel.setText("-" + formattedAmount);
                amountLabel.getStyleClass().removeAll("expense", "income");
                amountLabel.getStyleClass().add("expense");
            } else {
                amountLabel.setText(formattedAmount);
            }

            setGraphic(card);
        }
    }
}