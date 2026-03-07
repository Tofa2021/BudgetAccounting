package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
import org.example.client.viewModel.AuthViewModel;

public class AuthController extends BaseController<AuthViewModel> {
    @FXML
    public Button signInButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorLabel;

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

        errorLabel.textProperty().bindBidirectional(
                viewModel.getErrorMessage()
        );
    }

    @FXML
    private void handleSignIn() {
        viewModel.signIn(usernameField.getText(), passwordField.getText());
        passwordField.clear();
    }

    @FXML
    private void handleSignUpLink() {
        SceneManager.getInstance().loadScene(Scene.REGISTRATION);
    }
}
