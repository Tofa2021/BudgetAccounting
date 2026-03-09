package org.example.client.controller.filter;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.Setter;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;

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
