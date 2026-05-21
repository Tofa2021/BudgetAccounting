package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.example.client.viewModel.AuthViewModel;

public class AuthView extends BaseView<AuthViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button signInButton;

    @FXML
    private Hyperlink signUpLink;

    @FXML
    public void initialize() {
        setupEnterKeyNavigation();
    }

    @Override
    public void onViewModelSet() {
        usernameField.textProperty().bindBidirectional(getViewModel().getUsernameInput());
        passwordField.textProperty().bindBidirectional(getViewModel().getPasswordInput());
        errorLabel.textProperty().bindBidirectional(getViewModel().getErrorMessage());
        errorLabel.managedProperty().bind(getViewModel().getErrorMessage().isNotEmpty());
        errorLabel.visibleProperty().bind(getViewModel().getErrorMessage().isNotEmpty());
    }

    private void setupEnterKeyNavigation() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSignInButton();
            }
        });
    }

    @FXML
    private void handleSignInButton() {
        getViewModel().signIn();
    }

    @FXML
    private void handleSignUpLink() {
        getViewModel().linkSignUp();
    }
}
