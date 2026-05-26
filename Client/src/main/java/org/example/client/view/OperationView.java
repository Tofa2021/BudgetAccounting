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
import javafx.util.StringConverter;
import org.example.client.Utils;
import org.example.client.component.AmountTextField;
import org.example.client.viewModel.OperationViewModel;
import org.example.dto.AccountDTO;
import org.example.dto.CategoryDTO;
import org.example.dto.HouseholdMemberDTO;
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
    @FXML
    private TextField searchTextField;
    @FXML
    private Button toggleFilterButton;
    @FXML
    private Button closeFilterButton;
    @FXML
    private VBox filterPanel;
    @FXML
    private AmountTextField minAmountTextField;
    @FXML
    private AmountTextField maxAmountTextField;
    @FXML
    private ComboBox<CategoryDTO> categoryComboBox;
    @FXML
    private ComboBox<AccountDTO> accountComboBox;
    @FXML
    private ComboBox<HouseholdMemberDTO> creatorComboBox;
    @FXML
    private ComboBox<String> operationTypeComboBox;

    @Override
    public void onViewModelSet() {
        setupListView();
        bind();
        setupContextMenu();
    }

    private void setupListView() {
        operationsList.setCellFactory(listView -> new OperationCell());
        operationsList.setPlaceholder(new Label("Нет операций Нажмите + чтобы добавить"));
    }

    private void bind() {
        operationsList.setItems(getViewModel().getOperations());

        getViewModel().getIncomes().addListener((ListChangeListener<? super String>) c -> {
            updateIncomes();
        });

        getViewModel().getExpenses().addListener((ListChangeListener<? super String>) c -> {
            updateExpenses();
        });

        getViewModel().getProfits().addListener((ListChangeListener<? super String>) c -> {
            updateProfits();
        });

        getViewModel().getAmountOperations().addListener((ListChangeListener<? super String>) c -> {
            updateAmountOperations();
        });

        getViewModel().getSearchText().bindBidirectional(searchTextField.textProperty());

        filterPanel.visibleProperty().bind(getViewModel().getFilterPanelVisible());

        searchTextField.setOnAction(event -> {
            getViewModel().searchOperations();
        });

        searchTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                getViewModel().searchOperations();
            }
        });

        categoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryDTO categoryDTO) {
                if (categoryDTO == null) return "Все";
                return categoryDTO.getName();
            }

            @Override
            public CategoryDTO fromString(String string) {
                return null;
            }
        });
        categoryComboBox.valueProperty().bindBidirectional(getViewModel().getFilterCategory());
        categoryComboBox.itemsProperty().bind(getViewModel().getCategories());

        accountComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(AccountDTO accountDTO) {
                if (accountDTO == null) return "Все";
                return accountDTO.getName();
            }

            @Override
            public AccountDTO fromString(String string) {
                return null;
            }
        });
        accountComboBox.valueProperty().bindBidirectional(getViewModel().getFilterAccount());
        accountComboBox.itemsProperty().bind(getViewModel().getAccounts());

        creatorComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(HouseholdMemberDTO memberDTO) {
                if (memberDTO == null) return "";
                return memberDTO.getUsername();
            }

            @Override
            public HouseholdMemberDTO fromString(String string) {
                return null;
            }
        });
        creatorComboBox.valueProperty().bindBidirectional(getViewModel().getFilterCreator());
        creatorComboBox.itemsProperty().bind(getViewModel().getMembers());

        operationTypeComboBox.valueProperty().bindBidirectional(getViewModel().getFilterOperationType());
        operationTypeComboBox.itemsProperty().bind(getViewModel().getOperationTypes());

        minAmountTextField.amountProperty().bindBidirectional(getViewModel().getFilterMinAmount());
        minAmountTextField.setOnAction(event -> {
            minAmountTextField.getParent().requestFocus();
            double value = getViewModel().handleMinAmountAction();
            minAmountTextField.setAmount(value);
        });

        maxAmountTextField.amountProperty().bindBidirectional(getViewModel().getFilterMaxAmount());
        maxAmountTextField.setOnAction(event -> {
            maxAmountTextField.getParent().requestFocus();
            double value = getViewModel().handleMaxAmountAction();
            maxAmountTextField.setAmount(value);
        });
    }

    private void updateIncomes() {
        incomes.getChildren().clear();
        for (String text : getViewModel().getIncomes()) {
            addIncomeLabel(text);
        }
    }

    private void updateExpenses() {
        expenses.getChildren().clear();
        for (String text : getViewModel().getExpenses()) {
            addExpenseLabel(text);
        }
    }

    private void updateProfits() {
        profits.getChildren().clear();
        for (String text : getViewModel().getProfits()) {
            addProfitLabel(text);
        }
    }

    private void updateAmountOperations() {
        amountOperations.getChildren().clear();
        for (String text : getViewModel().getAmountOperations()) {
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
        getViewModel().handleCreateOperationButton();
    }

    @FXML
    private void handleFilterButton() {
        getViewModel().handleToggleFilterButton();
    }

    @FXML
    private void handleCloseFilterButton() {
        getViewModel().handleCloseFilterButton();
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
                            operation.getCurrency(),
                            operation.getHouseholdMemberId()
                    );
                } catch (Exception e) {
                    Platform.runLater(() -> showError("Ошибка ввода данных: " + e.getMessage()));
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedDto -> {
            getViewModel().update(updatedDto);
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
                getViewModel().delete(operation);
                showSuccess("Операция удалена");
            }
        });
    }

    @FXML
    private void handleFilter() {
        getViewModel().handleFilter();
    }

    @FXML
    private void handleFilterOperationType() {
        getViewModel().handleFilterOperationType();
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