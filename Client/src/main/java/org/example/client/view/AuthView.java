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
    protected void onViewModelSet() {
        usernameField.textProperty().bindBidirectional(this.viewModel.getUsernameInput());
        passwordField.textProperty().bindBidirectional(this.viewModel.getPasswordInput());
        errorLabel.textProperty().bindBidirectional(this.viewModel.getErrorMessage());
        errorLabel.managedProperty().bind(this.viewModel.getErrorMessage().isNotEmpty());
        errorLabel.visibleProperty().bind(this.viewModel.getErrorMessage().isNotEmpty());
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
        this.viewModel.signIn();
    }

    @FXML
    private void handleSignUpLink() {
        this.viewModel.linkSignUp();
    }
}
