package org.example.client.view;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
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
    public TextField amountTextField;
    @FXML
    public TextField descriptionTextField;
    @FXML
    public ComboBox<CategoryDTO> categoryComboBox;
    @FXML
    public Button createButton;
    @FXML
    public Label errorLabel;

    @Override
    protected void onViewModelSet() {
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

        accountComboBox.itemsProperty().bind(viewModel.getAccounts());

        accountComboBox.valueProperty().bindBidirectional(viewModel.getSelectedAccount());
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

        categoryComboBox.itemsProperty().bind(viewModel.getCategories());

        categoryComboBox.valueProperty().bindBidirectional(viewModel.getSelectedCategory());
    }

    private void setupBindings() {
        descriptionTextField.textProperty().bindBidirectional(viewModel.getDescription());
        errorLabel.textProperty().bind(viewModel.getErrorMessage());
        errorLabel.managedProperty().bind(viewModel.getErrorMessage().isNotEmpty());
        errorLabel.visibleProperty().bind(viewModel.getErrorMessage().isNotEmpty());
    }

    private void setupAmountField() {
        amountTextField.textProperty().addListener((obs, old, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                if (newValue.matches("\\d*[\\.\\,]?\\d{0,2}")) {
                    String normalizedValue = newValue.replace(",", ".");
                    try {
                        double amount = Double.parseDouble(normalizedValue);
                        if (amount > 0) {
                            viewModel.getAmount().set(amount);
                            amountTextField.setStyle("");
                        } else {
                            viewModel.getAmount().set(0);
                            amountTextField.setStyle("-fx-border-color: #f44336;");
                        }
                    } catch (NumberFormatException e) {
                        viewModel.getAmount().set(0);
                        amountTextField.setStyle("-fx-border-color: #f44336;");
                    }
                } else if (!newValue.isEmpty()) {
                    Platform.runLater(() -> amountTextField.setText(old));
                }
            } else {
                viewModel.getAmount().set(0);
                if (newValue != null && newValue.isEmpty()) {
                    amountTextField.setStyle("");
                }
            }
        });

        amountTextField.textProperty().bindBidirectional(viewModel.getAmountText());
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

        viewModel.onIncomeButtonAction();
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

        viewModel.onExpenseButtonAction();
    }

    public void handleCreateButton() {
        if (viewModel.getAmount().get() <= 0) {
            viewModel.getErrorMessage().set("Введите корректную сумму");
            amountTextField.requestFocus();
            return;
        }
        if (viewModel.getDescription().get() == null || viewModel.getDescription().get().trim().isEmpty()) {
            viewModel.getErrorMessage().set("Введите описание операции");
            descriptionTextField.requestFocus();
            return;
        }

        if (viewModel.getSelectedAccount().get() == null) {
            viewModel.getErrorMessage().set("Выберите счет");
            accountComboBox.requestFocus();
            return;
        }

        if (viewModel.getSelectedCategory().get() == null) {
            viewModel.getErrorMessage().set("Выберите категорию");
            categoryComboBox.requestFocus();
            return;
        }

        viewModel.getErrorMessage().set("");
        viewModel.createOperation();
    }
}
