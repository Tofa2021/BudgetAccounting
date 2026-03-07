package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.client.viewModel.BudgetViewModel;
import org.example.dto.DecreaseOperationCategory;
import org.example.dto.IncreaseOperationCategory;

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
    private ComboBox<IncreaseOperationCategory> increaseCategoryComboBox;
    @FXML
    private ComboBox<DecreaseOperationCategory> decreaseCategoryComboBox;
    @FXML
    private Label errorLabel;

    @FXML
    public void init() {
        increaseCategoryComboBox.getItems().setAll(IncreaseOperationCategory.values());
        decreaseCategoryComboBox.getItems().setAll(DecreaseOperationCategory.values());

        increaseCategoryComboBox.setValue(IncreaseOperationCategory.SALARY);
        decreaseCategoryComboBox.setValue(DecreaseOperationCategory.FOOD);
    }

    protected void bindViewModel() {
        balanceLabel.textProperty().bind(
                viewModel.getBalance().asString("Текущий баланс: $%d")
        );

        amountField.textProperty().bindBidirectional(
                viewModel.getAmountInput()
        );

        increaseCategoryComboBox.valueProperty().bindBidirectional(
                viewModel.getSelectedIncreaseCategory()
        );

        decreaseCategoryComboBox.valueProperty().bindBidirectional(
                viewModel.getSelectedDecreaseCategory()
        );

        errorLabel.textProperty().bind(
                viewModel.getErrorMessage());
    }

    @FXML
    private void handleIncrease() {
        if (isValidatedInput()) {
            int amount = Integer.parseInt(amountField.getText());
            viewModel.increase(amount, increaseCategoryComboBox.getValue());
            amountField.clear();
        }
    }

    @FXML
    private void handleDecrease() {
        if (isValidatedInput()) {
            int amount = Integer.parseInt(amountField.getText());
            viewModel.decrease(amount, decreaseCategoryComboBox.getValue());
            amountField.clear();
        }
    }

    private boolean isValidatedInput() {
        String text = amountField.getText();
        if (text == null || text.isEmpty()) {
            return false;
        }

        try {
            int amount = Integer.parseInt(text);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}