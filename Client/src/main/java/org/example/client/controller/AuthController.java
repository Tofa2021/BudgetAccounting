package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.client.viewModel.AuthViewModel;

public class AuthController {
    private AuthViewModel viewModel;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    public void setAuthViewModel(AuthViewModel viewModel) {
        this.viewModel = viewModel;
        bindViewModel();
    }

    private void bindViewModel() {
        usernameField.textProperty().bindBidirectional(
                viewModel.getUsernameInput()
        );

        passwordField.textProperty().bindBidirectional(
                viewModel.getPasswordInput()
        );
    }

    @FXML
    private void handleSignIn() {
        viewModel.signIn(usernameField.getText(), passwordField.getText());
    }
}
