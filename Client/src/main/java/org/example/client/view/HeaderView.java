package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.example.client.viewModel.HeaderViewModel;

public class HeaderView extends BaseView<HeaderViewModel> {
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button profileButton;

    @Override
    protected void onViewModelSet() {
        totalAmountLabel.textProperty().bind(viewModel.getTotalAmount());
        userNameLabel.textProperty().bind(viewModel.getUserName());
    }

    public void handleProfileButton() {
        viewModel.logout();
    }
}