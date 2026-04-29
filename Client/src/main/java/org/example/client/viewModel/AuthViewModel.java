package org.example.client.viewModel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import org.example.client.connection.ServerInteractionManager;
import org.example.client.scene.Scene;
import org.example.client.scene.SceneManager;

@Getter
public class AuthViewModel extends BaseViewModel {
    private final StringProperty usernameInput = new SimpleStringProperty("");
    private final StringProperty passwordInput = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    public AuthViewModel(ServerInteractionManager serverInteractionManager) {
        super(serverInteractionManager);
    }

    @Override
    public void init() {
    }

    public boolean signIn(String username, String password) {
        if (!isInputCorrect()) {
            return false;
        }

        var result = serverInteractionManager.signIn(username, password);
        if (result.isSuccess()) {
            SceneManager.getInstance().loadScene(Scene.BUDGET);
            return true;
        }
        errorMessage.setValue(result.getErrorMessage());
        return false;
    }

    private boolean isInputCorrect() {
        String usernameValue = usernameInput.getValue();
        String passwordValue = passwordInput.getValue();

        if (usernameValue.isEmpty()) {
            errorMessage.setValue("Логин не может быть пустым");
            return false;
        }

        if (passwordValue.isEmpty()) {
            errorMessage.setValue("Пароль не может быть пустым");
            return false;
        }

        return true;
    }
}
