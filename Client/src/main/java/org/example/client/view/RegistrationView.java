package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import org.example.client.viewModel.RegistrationViewModel;

public class RegistrationView extends BaseView<RegistrationViewModel> {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmationPasswordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button signUpButton;

    @FXML
    private Hyperlink signInLink;

    @Override
    protected void onViewModelSet() {
        usernameField.textProperty().bindBidirectional(viewModel.getUsernameInput());
        passwordField.textProperty().bindBidirectional(viewModel.getPasswordInput());
        confirmationPasswordField.textProperty().bindBidirectional(viewModel.getConfirmationPasswordInput());
        errorLabel.textProperty().bindBidirectional(viewModel.getErrorMessage());
        errorLabel.visibleProperty().bind(viewModel.getErrorMessage().isNotEmpty());
    }

    @FXML
    public void initialize() {
        setupEnterKeyNavigation();
    }

    private void setupEnterKeyNavigation() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmationPasswordField.requestFocus();
            }
        });

        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSignUpButton();
            }
        });
    }

    @FXML
    private void handleSignUpButton() {
        viewModel.signUp();
    }

    @FXML
    private void handleSignInLink() {
        viewModel.linkSignIn();
    }
}
