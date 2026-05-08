package org.example.client.controller.filter;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.OperationCategory;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;

@Setter
public class OperationFilterController {
    @FXML
    private TitledPane titledPane;
    @FXML
    private ComboBox<String> typeFilterComboBox;
    @FXML
    private ComboBox<String> categoryFilterComboBox;
    @FXML
    private DatePicker dateFromPicker;
    @FXML
    private DatePicker dateToPicker;
    @FXML
    private Button clearDateButton;
    @FXML
    private TextField minAmountField;
    @FXML
    private TextField maxAmountField;
    @FXML
    private Button applyFiltersButton;
    @FXML
    private Button clearFiltersButton;
    @FXML
    private Label filterResultsLabel;

    private FilterListener listener;

    @FXML
    public void initialize() {
        setupFilters();
        setupAmountValidation();
    }

    private void setupFilters() {
        typeFilterComboBox.getItems().setAll("Все", "Доходы", "Расходы");
        typeFilterComboBox.setValue("Все");

        categoryFilterComboBox.getItems().setAll("Все категории");
        categoryFilterComboBox.setValue("Все категории");

        typeFilterComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateCategoryFilter(newVal);
        });
    }

    private void setupAmountValidation() {
        TextFormatter<String> minFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }

            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        });

        TextFormatter<String> maxFormatter = new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.isEmpty()) {
                return change;
            }

            if (newText.matches("\\d*(\\.\\d{0,2})?")) {
                return change;
            }
            return null;
        });

        minAmountField.setTextFormatter(minFormatter);
        maxAmountField.setTextFormatter(maxFormatter);
    }

    private void updateCategoryFilter(String type) {
        categoryFilterComboBox.getItems().clear();
        categoryFilterComboBox.getItems().add("Все категории");

        switch (type) {
            case "Доходы" -> {
                categoryFilterComboBox.getItems().addAll(Arrays.stream(
                                OperationCategory.values())
                        .map(OperationCategory::getName)
                        .toList());
            }
            case "Расходы" -> categoryFilterComboBox.getItems().addAll(Arrays.stream(
                            DecreaseOperationCategory.values())
                    .map(DecreaseOperationCategory::getName)
                    .toList());
        }
        categoryFilterComboBox.setValue("Все категории");
    }

    @FXML
    private void handleApplyFilters() {
        String type =
                switch (typeFilterComboBox.getValue()) {
                    case "Доходы" -> "+";
                    case "Расходы" -> "-";
                    default -> "Все";
                };
        String category = categoryFilterComboBox.getValue();
        Instant dateFrom = dateFromPicker.getValue() != null
                ? dateFromPicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()
                : null;
        Instant dateTo = dateToPicker.getValue() != null
                ? dateToPicker.getValue().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()
                : null;
        Double minAmount = minAmountField.getText().isEmpty()
                ? null
                : Double.parseDouble(minAmountField.getText());

        Double maxAmount = maxAmountField.getText().isEmpty()
                ? null
                : Double.parseDouble(maxAmountField.getText());

        listener.onFiltersApplied(type, category, dateFrom, dateTo, minAmount, maxAmount);
        titledPane.setExpanded(false);
    }

    public void clearFilters() {
        categoryFilterComboBox.getSelectionModel().selectFirst();
        typeFilterComboBox.getSelectionModel().selectFirst();
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
        minAmountField.clear();
        maxAmountField.clear();
    }

    public void setExpanded(boolean b) {
        titledPane.setExpanded(b);
    }

    @FXML
    private void handleClearFilters() {
        clearFilters();
        listener.onFiltersCleared();
        titledPane.setExpanded(false);
    }

    @FXML
    public void handleClearDates() {
        dateFromPicker.setValue(null);
        dateToPicker.setValue(null);
    }
}
