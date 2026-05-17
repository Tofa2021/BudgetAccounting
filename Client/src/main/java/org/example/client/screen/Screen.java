package org.example.client.screen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Screen {
    AUTH("/org/example/client/view/AuthView.fxml"),
    REGISTRATION("/org/example/client/view/RegistrationView.fxml"),
    MAIN("/org/example/client/view/OperationView.fxml"),
    HOUSEHOLDS("/org/example/client/view/HouseholdView.fxml"),
    ACCOUNTS("/org/example/client/view/AccountsView.fxml"),
    OPERATIONS("/org/example/client/view/OperationView.fxml"),
    CREATING_OPERATION("/org/example/client/view/CreatingOperationView.fxml"),
    GRAPHICS("/org/example/client/view/GraphicsView.fxml"),
    PROFILE("/org/example/client/view/ProfileView.fxml"),
    PLANNING("/org/example/client/view/PlanningView.fxml"),
    REPORTS("/org/example/client/view/ReportsView.fxml"),
    SETTINGS("/org/example/client/view/SettingsView.fxml"),
    CREATING_HOUSEHOLD("/org/example/client/view/CreatingHousehold.fxml"),
    ;

    private final String fxmlPath;
}
