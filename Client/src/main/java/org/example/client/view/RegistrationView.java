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
    public void onViewModelSet() {
        usernameField.textProperty().bindBidirectional(getViewModel().getUsernameInput());
        passwordField.textProperty().bindBidirectional(getViewModel().getPasswordInput());
        confirmationPasswordField.textProperty().bindBidirectional(getViewModel().getConfirmationPasswordInput());
        errorLabel.textProperty().bindBidirectional(getViewModel().getErrorMessage());
        errorLabel.managedProperty().bind(getViewModel().getErrorMessage().isNotEmpty());
        errorLabel.visibleProperty().bind(getViewModel().getErrorMessage().isNotEmpty());
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

        confirmationPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSignUpButton();
            }
        });
    }

    @FXML
    private void handleSignUpButton() {
        getViewModel().signUp();
    }

    @FXML
    private void handleSignInLink() {
        getViewModel().linkSignIn();
    }
}
