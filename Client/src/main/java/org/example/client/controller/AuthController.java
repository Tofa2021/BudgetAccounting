package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.example.client.viewModel.AuthViewModel;

public class AuthController extends BaseController<AuthViewModel> {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;

    @Override
    public void initialize() {
    }

    @Override
    protected void bindViewModel() {
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
