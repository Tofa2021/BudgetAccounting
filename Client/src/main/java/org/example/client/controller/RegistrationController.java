package org.example.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;
import org.example.client.viewModel.RegistrationViewModel;

public class RegistrationController extends BaseController<RegistrationViewModel> {
    @FXML
    public Button signUpButton;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label errorField;

    @Override
    protected void bindViewModel() {
        usernameField.textProperty().bindBidirectional(
                viewModel.getUsernameInput()
        );

        passwordField.textProperty().bindBidirectional(
                viewModel.getPasswordInput()
        );

        errorField.textProperty().bindBidirectional(
                viewModel.getErrorInput()
        );
    }

    @FXML
    private void handleSignUp() {
        viewModel.signUp(usernameField.getText(), passwordField.getText());
        passwordField.clear();
    }

    @FXML
    private void handleSignInLink() {
        SceneManager.getInstance().loadScene(Scene.AUTH);
    }
}
