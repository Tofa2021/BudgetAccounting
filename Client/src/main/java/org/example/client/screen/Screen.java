package org.example.client.screen;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Screen {
    AUTH("/org/example/client/view/AuthView.fxml"),
    REGISTRATION("/org/example/client/view/RegistrationView.fxml"),
    MAIN("/org/example/client/view/AuthView.fxml"),
    HOUSEHOLDS("/org/example/client/view/AuthView.fxml"),
    ACCOUNTS("/org/example/client/view/AuthView.fxml"),
    OPERATIONS("/org/example/client/view/AuthView.fxml"),
    GRAPHICS("/org/example/client/view/AuthView.fxml"),
    PROFILE("/org/example/client/view/AuthView.fxml"),
    ;

    private final String fxmlPath;
}
