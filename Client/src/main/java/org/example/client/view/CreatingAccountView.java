package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import org.example.client.viewModel.CreatingAccountViewModel;
import org.example.enums.Currency;

public class CreatingAccountView extends BaseView<CreatingAccountViewModel> {
    @FXML
    private TextField nameTextField;
    @FXML
    private ComboBox<Currency> currencyComboBox;
    @FXML
    private Button createButton;

    @Override
    public void onViewModelSet() {
        nameTextField.textProperty().bindBidirectional(getViewModel().getNameProperty());
        currencyComboBox.setItems(getViewModel().getCurrencies());
        currencyComboBox.valueProperty().bindBidirectional(getViewModel().getSelectedCurrency());
    }

    @FXML
    private void handleCreateButton() {
        getViewModel().handleCreateButton();
    }
}
