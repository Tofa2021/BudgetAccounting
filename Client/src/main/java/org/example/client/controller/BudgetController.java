package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
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

    @FXML
    public void init() {
    }

    protected void bindViewModel() {
        balanceLabel.textProperty().bind(
                viewModel.getBalance().asString("Текущий баланс: $%d")
        );

        amountField.textProperty().bindBidirectional(
                viewModel.getAmountInput()
        );

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

    @FXML
    private void handleIncrease() {
        if (isValidatedInput()) {
            int amount = Integer.parseInt(amountField.getText());
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
            int amount = Integer.parseInt(amountField.getText());
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
            int amount = Integer.parseInt(text);
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @FXML
    public void handleOpenOperationHistory() {
        SceneManager.getInstance().loadScene(Scene.OPERATION_HISTORY);
    }
}