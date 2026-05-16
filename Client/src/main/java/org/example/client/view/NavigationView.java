package org.example.client.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.example.client.screen.Screen;
import org.example.client.viewModel.NavigationViewModel;

public class NavigationView extends BaseView<NavigationViewModel> {
    private static final String SELECTED_BUTTON_STYLE = "selected-navigation-button";
    private static final String BUTTON_STYLE = "navigation-button";
    @FXML
    private Button mainButton;
    @FXML
    private Button operationButton;
    @FXML
    private Button accountButton;
    @FXML
    private Button graphicsButton;
    @FXML
    private Button householdButton;
    @FXML
    private Button settingsButton;

    @Override
    protected void onViewModelSet() {
        viewModel.getCurrentScreen().addListener((obs, old, newScreen) -> {
            updateButtonActiveState(newScreen);
        });
    }

    private void updateButtonActiveState(Screen activeScreen) {
        resetAllButtonStyles();

        Button activeButton = getButtonForScreen(activeScreen);
        if (activeButton != null) {
            activeButton.getStyleClass().remove(BUTTON_STYLE);
            activeButton.getStyleClass().add(SELECTED_BUTTON_STYLE);
        }
    }

    private Button getButtonForScreen(Screen screen) {
        if (screen == null) return null;

        return switch (screen) {
            case MAIN -> mainButton;
            case OPERATIONS -> operationButton;
            case ACCOUNTS -> accountButton;
            case GRAPHICS -> graphicsButton;
            case HOUSEHOLDS -> householdButton;
            case SETTINGS -> settingsButton;
            default -> null;
        };
    }

    private void resetAllButtonStyles() {
        mainButton.getStyleClass().setAll(BUTTON_STYLE);
        operationButton.getStyleClass().setAll(BUTTON_STYLE);
        accountButton.getStyleClass().setAll(BUTTON_STYLE);
        graphicsButton.getStyleClass().setAll(BUTTON_STYLE);
        householdButton.getStyleClass().setAll(BUTTON_STYLE);
        settingsButton.getStyleClass().setAll(BUTTON_STYLE);
    }

    public void handleMainButton() {
        viewModel.navigateTo(Screen.MAIN);
    }

    public void handleOperationButton() {
        viewModel.navigateTo(Screen.OPERATIONS);
    }

    public void handleAccountButton() {
        viewModel.navigateTo(Screen.ACCOUNTS);
    }

    public void handleGraphicsButton() {
        viewModel.navigateTo(Screen.GRAPHICS);
    }

    public void handleHouseholdButton() {
        viewModel.navigateTo(Screen.HOUSEHOLDS);
    }

    public void handleSettingsButton() {
        viewModel.navigateTo(Screen.SETTINGS);
    }
}