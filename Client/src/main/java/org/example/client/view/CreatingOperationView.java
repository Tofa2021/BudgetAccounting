package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.example.client.component.AmountTextField;
import org.example.client.viewModel.CreatingOperationViewModel;
import org.example.dto.AccountDTO;
import org.example.dto.CategoryDTO;

public class CreatingOperationView extends BaseView<CreatingOperationViewModel> {
    private final String OPERATION_BUTTON_STYLE = "operation-button";
    private final String SELECTED_OPERATION_BUTTON_STYLE = "selected-operation-button";
    private final String BUTTON_STYLE = "button";
    @FXML
    public Button incomeButton;
    @FXML
    public Button expenseButton;
    @FXML
    public ComboBox<AccountDTO> accountComboBox;
    @FXML
    public AmountTextField amountTextField;
    @FXML
    public TextField descriptionTextField;
    @FXML
    public ComboBox<CategoryDTO> categoryComboBox;
    @FXML
    public Button createButton;
    @FXML
    public Label errorLabel;

    @Override
    public void onViewModelSet() {
        incomeButton.getStyleClass().add(SELECTED_OPERATION_BUTTON_STYLE);
        incomeButton.getStyleClass().remove(OPERATION_BUTTON_STYLE);
        incomeButton.setDisable(true);
        setupAccountComboBox();
        setupCategoryComboBox();
        setupBindings();
        setupAmountField();
        setupValidation();
    }

    private void setupAccountComboBox() {
        accountComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(AccountDTO account) {
                if (account == null) return "";
                return account.getName();
            }

            @Override
            public AccountDTO fromString(String string) {
                return null;
            }
        });

        accountComboBox.itemsProperty().bind(getViewModel().getAccounts());

        accountComboBox.valueProperty().bindBidirectional(getViewModel().getSelectedAccount());
    }

    private void setupCategoryComboBox() {
        categoryComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(CategoryDTO category) {
                if (category == null) return "";
                return category.getName();
            }

            @Override
            public CategoryDTO fromString(String string) {
                return null;
            }
        });

        categoryComboBox.itemsProperty().bind(getViewModel().getCategories());

        categoryComboBox.valueProperty().bindBidirectional(getViewModel().getSelectedCategory());
    }

    private void setupBindings() {
        descriptionTextField.textProperty().bindBidirectional(getViewModel().getDescription());
        errorLabel.textProperty().bind(getViewModel().getErrorMessage());
        errorLabel.managedProperty().bind(getViewModel().getErrorMessage().isNotEmpty());
        errorLabel.visibleProperty().bind(getViewModel().getErrorMessage().isNotEmpty());
    }

    private void setupAmountField() {
        amountTextField.amountProperty().bindBidirectional(getViewModel().getAmount());

        amountTextField.textProperty().bindBidirectional(getViewModel().getAmountText());
    }

    private void setupValidation() {
        descriptionTextField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                descriptionTextField.setStyle("-fx-border-color: #f44336;");
            } else {
                descriptionTextField.setStyle("");
            }
        });

        accountComboBox.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                accountComboBox.setStyle("-fx-border-color: #f44336;");
            } else {
                accountComboBox.setStyle("");
            }
        });

        categoryComboBox.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue == null) {
                categoryComboBox.setStyle("-fx-border-color: #f44336;");
            } else {
                categoryComboBox.setStyle("");
            }
        });
    }


    public void handleIncomeButton() {
        expenseButton.getStyleClass().clear();
        incomeButton.getStyleClass().clear();

        incomeButton.getStyleClass().add(SELECTED_OPERATION_BUTTON_STYLE);
        incomeButton.getStyleClass().add(BUTTON_STYLE);
        incomeButton.setDisable(true);
        expenseButton.getStyleClass().add(OPERATION_BUTTON_STYLE);
        expenseButton.getStyleClass().add(BUTTON_STYLE);
        expenseButton.setDisable(false);

        getViewModel().onIncomeButtonAction();
    }

    public void handleExpenseButton() {
        expenseButton.getStyleClass().clear();
        incomeButton.getStyleClass().clear();

        expenseButton.getStyleClass().add(SELECTED_OPERATION_BUTTON_STYLE);
        expenseButton.getStyleClass().add(BUTTON_STYLE);
        expenseButton.setDisable(true);
        incomeButton.getStyleClass().add(OPERATION_BUTTON_STYLE);
        incomeButton.getStyleClass().add(BUTTON_STYLE);
        incomeButton.setDisable(false);

        getViewModel().onExpenseButtonAction();
    }

    public void handleCreateButton() {
        if (getViewModel().getAmount().get() <= 0) {
            getViewModel().getErrorMessage().set("Введите корректную сумму");
            amountTextField.requestFocus();
            return;
        }
        if (getViewModel().getDescription().get() == null || getViewModel().getDescription().get().trim().isEmpty()) {
            getViewModel().getErrorMessage().set("Введите описание операции");
            descriptionTextField.requestFocus();
            return;
        }

        if (getViewModel().getSelectedAccount().get() == null) {
            getViewModel().getErrorMessage().set("Выберите счет");
            accountComboBox.requestFocus();
            return;
        }

        if (getViewModel().getSelectedCategory().get() == null) {
            getViewModel().getErrorMessage().set("Выберите категорию");
            categoryComboBox.requestFocus();
            return;
        }

        getViewModel().getErrorMessage().set("");
        getViewModel().createOperation();
    }
}
