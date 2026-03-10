package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.client.viewModel.BudgetViewModel;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;

import java.util.Arrays;

public class BudgetController extends BaseController<BudgetViewModel> {
    @FXML
    private Label balanceLabel;
    @FXML
    private TextField amountField;
    @FXML
    private Button increaseButton;
    @FXML
    private Button decreaseButton;
    @FXML
    private ComboBox<String> increaseCategoryComboBox;
    @FXML
    private ComboBox<String> decreaseCategoryComboBox;
    @FXML
    private Label errorLabel;

    protected void bindViewModel() {
        balanceLabel.textProperty().bind(
                viewModel.getBalance().asString("Текущий баланс: %.2f руб")
        );

        amountField.textProperty().bindBidirectional(
                viewModel.getAmountInput()
        );

        setupAmountValidation();

        increaseCategoryComboBox.getItems().setAll(Arrays.stream(IncreaseOperationCategory.values())
                .map(IncreaseOperationCategory::getName)
                .toList());
        increaseCategoryComboBox.valueProperty().bindBidirectional(
                viewModel.getSelectedIncreaseCategory()
        );

        decreaseCategoryComboBox.getItems().setAll(Arrays.stream(DecreaseOperationCategory.values())
                .map(DecreaseOperationCategory::getName)
                .toList());
        decreaseCategoryComboBox.valueProperty().bindBidirectional(
                viewModel.getSelectedDecreaseCategory()
        );

        errorLabel.textProperty().bind(
                viewModel.getErrorMessage());
    }

    private void setupAmountValidation() {
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return;
            }

            if (!newValue.matches("\\d*(\\.\\d{0,2})?")) {
                amountField.setText(oldValue);
            }
        });
    }

    @FXML
    private void handleIncrease() {
        if (isValidatedInput()) {
            double amount = Double.parseDouble(amountField.getText());
            String categoryString = increaseCategoryComboBox.getValue();
            IncreaseOperationCategory category = Arrays.stream(IncreaseOperationCategory.values())
                    .filter(cat -> cat.getName().equals(categoryString))
                    .findFirst()
                    .get();
            viewModel.increase(amount, category);
            amountField.clear();
        }
    }

    @FXML
    private void handleDecrease() {
        if (isValidatedInput()) {
            double amount = Double.parseDouble(amountField.getText());
            String categoryString = decreaseCategoryComboBox.getValue();
            DecreaseOperationCategory category = Arrays.stream(DecreaseOperationCategory.values())
                    .filter(cat -> cat.getName().equals(categoryString))
                    .findFirst()
                    .get();
            viewModel.decrease(amount, category);
            amountField.clear();
        }
    }

    private boolean isValidatedInput() {
        String text = amountField.getText();
        if (text == null || text.isEmpty()) {
            return false;
        }

        try {
            double amount = Double.parseDouble(text);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}