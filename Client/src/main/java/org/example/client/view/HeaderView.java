package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.example.client.viewModel.HeaderViewModel;

public class HeaderView extends BaseView<HeaderViewModel> {
    @FXML
    private ComboBox<String> currencyAmountComboBox;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button profileButton;

    @Override
    public void onViewModelSet() {
        userNameLabel.textProperty().bind(getViewModel().getUsername());
        profileButton.disableProperty().bind(getViewModel().getProfileButtonDisabled());
        currencyAmountComboBox.setItems(getViewModel().getCurrencyAmounts());
        getViewModel().getSelectedCurrencyAmount().addListener((observable, oldValue, newValue) -> {
            currencyAmountComboBox.valueProperty().set(newValue);
            currencyAmountComboBox.getParent().requestLayout();
        });
        currencyAmountComboBox.visibleProperty().bind(getViewModel().getCurrencyAmountComboBoxVisible());
    }

    public void handleProfileButton() {
        getViewModel().logout();
    }
}