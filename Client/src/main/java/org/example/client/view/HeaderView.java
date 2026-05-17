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
    protected void onViewModelSet() {
        userNameLabel.textProperty().bind(viewModel.getUsername());
        profileButton.disableProperty().bind(viewModel.getProfileButtonDisabled());
        currencyAmountComboBox.setItems(viewModel.getCurrencyAmounts());
        currencyAmountComboBox.valueProperty().bindBidirectional(viewModel.getSelectedCurrencyAmount());
        currencyAmountComboBox.visibleProperty().bind(viewModel.getCurrencyAmountComboBoxVisible());
    }

    public void handleProfileButton() {
        viewModel.logout();
    }
}